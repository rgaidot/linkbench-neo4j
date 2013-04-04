package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.*;
import com.facebook.LinkBench.Node;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static cz.bachman.linkbench.neo4j.IdGenerationRule.*;

/**
 * Connector to Neo4j.
 * <p/>
 * Subclasses should decide, which implementation of {@link GraphDatabaseService} to use and initialize/cleanup accordingly.
 */
public abstract class Neo4jGraphStore extends GraphStore {

    private static final Logger LOG = Logger.getLogger(Neo4jGraphStore.class);

    //names of Neo4j properties
    protected static final String ID = "id";
    protected static final String TIME = "time";
    protected static final String TYPE = "type";
    protected static final String VERSION = "version";
    protected static final String DATA = "data";
    protected static final String VISIBILITY = "visibility";

    protected GraphDatabaseService db;

    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void clearErrors(int i) {
        //no need to do anything
    }

    @Override
    public void resetNodeStore(String dbid, long startID) throws Exception {
        idGenerator.set(startID);
    }

    @Override
    public boolean addLink(String unusedParam1, Link link, boolean unusedParam2) throws Exception {
        final Relationship relationship = getRelationship(link);
        if (relationship == null) {
            addLinkInTransaction(link);
            return true;
        } else {
            updateLinkInTransaction(link, relationship);
            return false;
        }
    }

    @Override
    public boolean deleteLink(String unusedParam1, long nodeId1, long linkType, long nodeId2, boolean unusedParam2, boolean expunge) throws Exception {
        final Relationship relationship = getRelationship(linkType, nodeId1, nodeId2);
        if (relationship == null) {
            return false;
        }

        if (expunge) {
            deleteLinkInTransaction(relationship);
        } else {
            hideLinkInTransaction(relationship);
        }

        return true;
    }

    @Override
    public boolean updateLink(String unusedParam1, Link link, boolean unusedParam2) throws Exception {
        return !addLink(unusedParam1, link, unusedParam2);
    }

    @Override
    public Link getLink(String unusedParam, long nodeId1, long linkType, long nodeId2) throws Exception {
        final Relationship relationship = getRelationship(linkType, nodeId1, nodeId2);
        if (relationship != null) {
            return relationshipToLink(relationship);
        }

        return null;
    }

    @Override
    public Link[] getLinkList(String unusedParam, long nodeId, long linkType) throws Exception {
        return getLinkList(unusedParam, nodeId, linkType, 0, Long.MAX_VALUE, 0, rangeLimit);
    }

    @Override
    public Link[] getLinkList(String unusedParam, long nodeId, long linkType, long minTimestamp, long maxTimestamp, int offset, int limit) throws Exception {
        ArrayList<Link> result = new ArrayList<>();

        org.neo4j.graphdb.Node neoNode = getNodeById(nodeId, false);
        if (neoNode == null) {
            LOG.warn("Node ID " + nodeId + " does not exits and it seems it should");
            return null;
        }

        Iterable<Relationship> relationships = neoNode.getRelationships(Direction.OUTGOING, linkTypeToRelationshipType(linkType));
        for (Relationship relationship : relationships) {
            if (VISIBILITY_HIDDEN == relationship.getProperty(VISIBILITY)) {
                continue;
            }

            long timestamp = (Long) relationship.getProperty(TIME);
            if (timestamp < minTimestamp || timestamp > maxTimestamp) {
                continue;
            }

            result.add(relationshipToLink(relationship));
        }

        Collections.sort(result, new Comparator<Link>() {
            @Override
            public int compare(Link link, Link link2) {
                if (link2.time > link.time) return 1;
                if (link2.time == link.time) return 0;
                return -1;
            }
        });

        if (result.isEmpty()) {
            return null;
        }

        return result.subList(offset, Math.min(result.size(), offset + limit)).toArray(new Link[Math.min(result.size(), limit)]);
    }

    @Override
    public long countLinks(String unusedParam, long nodeId, long linkType) throws Exception {
        Link[] links = getLinkList(unusedParam, nodeId, linkType);
        return links == null ? 0 : links.length;
    }

    @Override
    public long addNode(String unusedParam, final Node node) throws Exception {
        return createNodeInTransaction(node, GENERATE_ALWAYS);
    }

    @Override
    public Node getNode(String unusedParam, int type, long id) throws Exception {
        org.neo4j.graphdb.Node neoNode = getNodeByIdAndType(id, type);

        if (neoNode == null) {
            return null;
        }

        return neoNodeToNode(neoNode);
    }

    @Override
    public boolean updateNode(String unusedParam, final Node node) throws Exception {
        final org.neo4j.graphdb.Node neoNode = getNodeById(node.id, false);

        if (neoNode == null) {
            return false;
        }

        doInTransaction(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                nodeToNeoNode(neoNode, node);
                return null;
            }
        });

        return true;
    }

    @Override
    public boolean deleteNode(String unusedParam, final int type, final long id) throws Exception {
        return doInTransaction(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction() {
                org.neo4j.graphdb.Node neoNode = getNodeByIdAndType(id, type);

                if (neoNode == null) {
                    return false;
                }

                for (Relationship relationship : neoNode.getRelationships()) {
                    relationship.delete();
                }

                neoNode.delete();
                return true;
            }
        });
    }

    @Override
    public long[] bulkAddNodes(String dbid, final List<Node> nodes) throws Exception {
        final long[] result = new long[nodes.size()];

        doInTransaction(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                for (int i = 0; i < nodes.size(); i++) {
                    result[i] = createNodeInTransaction(nodes.get(i), GENERATE_IF_MINUS_ONE);
                }

                return null;
            }
        });

        return result;
    }

    @Override
    public int bulkLoadBatchSize() {
        return 10000;
    }

    @Override
    public void addBulkLinks(String dbid, final List<Link> links, boolean noinverse) throws Exception {
        doInTransaction(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                for (Link link : links) {
                    doAddLink(link);
                }

                return null;
            }
        });
    }

    @Override
    public void addBulkCounts(String dbid, List<LinkCount> a) throws Exception {
        //do nothing, links counted on the fly
    }

    //---------- Relationship manipulations ----------

    private void deleteLinkInTransaction(final Relationship relationship) {
        doInTransaction(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                relationship.delete();
                return null;
            }
        });
    }

    private void hideLinkInTransaction(final Relationship relationship) {
        doInTransaction(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                relationship.setProperty(VISIBILITY, VISIBILITY_HIDDEN);
                return null;
            }
        });
    }

    private void updateLinkInTransaction(final Link link, final Relationship relationship) {
        doInTransaction(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                relationship.setProperty(VISIBILITY, link.visibility);
                relationship.setProperty(DATA, link.data);
                relationship.setProperty(VERSION, link.version);
                relationship.setProperty(TIME, link.time);
                return null;
            }
        });
    }

    private void addLinkInTransaction(final Link link) {
        doInTransaction(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                doAddLink(link);
                return null;
            }
        });
    }

    protected void doAddLink(Link link) {
        final org.neo4j.graphdb.Node neoNode1 = getNodeById(link.id1, true);
        final org.neo4j.graphdb.Node neoNode2 = getNodeById(link.id2, true);

        Relationship relationship = neoNode1.createRelationshipTo(neoNode2, linkTypeToRelationshipType(link.link_type));
        relationship.setProperty(VISIBILITY, link.visibility);
        relationship.setProperty(DATA, link.data);
        relationship.setProperty(VERSION, link.version);
        relationship.setProperty(TIME, link.time);

        relationshipIndex().add(relationship, TYPE, relationship.getType().name());
    }

    private Relationship getRelationship(Link link) {
        return getRelationship(link.link_type, link.id1, link.id2);
    }

    private Relationship getRelationship(long linkType, long id1, long id2) {
        final org.neo4j.graphdb.Node neoNode1 = getNodeById(id1, false);
        final org.neo4j.graphdb.Node neoNode2 = getNodeById(id2, false);

        if (neoNode1 == null || neoNode2 == null) {
            return null;
        }

        return relationshipIndex().get(TYPE, String.valueOf(linkType), neoNode1, neoNode2).getSingle();
    }

    private RelationshipIndex relationshipIndex() {
        return db.index().forRelationships("relationshipsIndex");
    }

    //---------- (Neo) Node manipulations ------------

    private long createNodeInTransaction(final Node node, final IdGenerationRule idGenerationRule) {
        return doInTransaction(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction() {
                org.neo4j.graphdb.Node created = nodeToNeoNode(db.createNode(), node);

                if (GENERATE_ALWAYS.equals(idGenerationRule)
                        || (GENERATE_IF_MINUS_ONE.equals(idGenerationRule) && -1L == (Long) created.getProperty(ID))) {
                    long id = idGenerator.getAndIncrement();

                    //there could be nodes inserted from relationship creation
                    while (nodeIndex().get(ID, (Long) id).getSingle() != null) {
                        id = idGenerator.getAndIncrement();
                    }

                    created.setProperty(ID, id);
                }

                nodeIndex().add(created, ID, (Long) created.getProperty(ID));

                return (Long) created.getProperty(ID);
            }
        });
    }

    protected org.neo4j.graphdb.Node getNodeById(long id, boolean createIfMissing) {
        org.neo4j.graphdb.Node neoNode = nodeIndex().get(ID, id).getSingle();

        if (neoNode == null && createIfMissing) {
            createNodeInTransaction(new com.facebook.LinkBench.Node(id, DEFAULT_NODE_TYPE, 0, 0, new byte[0]), GENERATE_NEVER);
            return nodeIndex().get(ID, id).getSingle();
        }

        return neoNode;
    }

    private org.neo4j.graphdb.Node getNodeByIdAndType(long id, int type) {
        org.neo4j.graphdb.Node neoNode = getNodeById(id, false);

        if (neoNode == null || type != (Integer) neoNode.getProperty(TYPE)) {
            return null;
        }

        return neoNode;
    }

    protected Index<org.neo4j.graphdb.Node> nodeIndex() {
        return db.index().forNodes("nodesIndex");
    }

    //---------- Conversions ----------

    private Node neoNodeToNode(org.neo4j.graphdb.Node neoNode) {
        return new Node(
                (Long) neoNode.getProperty(ID),
                (Integer) neoNode.getProperty(TYPE),
                (Long) neoNode.getProperty(VERSION),
                (Integer) neoNode.getProperty(TIME),
                ((byte[]) neoNode.getProperty(DATA))
        );
    }

    protected org.neo4j.graphdb.Node nodeToNeoNode(org.neo4j.graphdb.Node neoNode, Node node) {
        neoNode.setProperty(ID, node.id);
        neoNode.setProperty(TIME, node.time);
        neoNode.setProperty(TYPE, node.type);
        neoNode.setProperty(VERSION, node.version);
        neoNode.setProperty(DATA, node.data);
        return neoNode;
    }

    private RelationshipType linkTypeToRelationshipType(long linkType) {
        return DynamicRelationshipType.withName(String.valueOf(linkType));
    }

    private Link relationshipToLink(Relationship relationship) {
        return new Link(
                (Long) relationship.getStartNode().getProperty(ID),
                Long.valueOf(relationship.getType().name()),
                (Long) relationship.getEndNode().getProperty(ID),
                (Byte) relationship.getProperty(VISIBILITY),
                (byte[]) relationship.getProperty(DATA),
                (Integer) relationship.getProperty(VERSION),
                (Long) relationship.getProperty(TIME)
        );
    }

    //---------- TX helpers ----------

    protected interface TransactionCallback<T> {
        T doInTransaction();
    }

    protected <T> T doInTransaction(TransactionCallback<T> transactionCallback) {
        T toReturn;

        Transaction tx = db.beginTx();
        try {
            toReturn = transactionCallback.doInTransaction();
            tx.success();
        } finally {
            tx.finish();
        }

        return toReturn;
    }


}
