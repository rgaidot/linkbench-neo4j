package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.store.LinkStore;

import static cz.bachman.linkbench.neo4j.Neo4jTestUtils.deleteDatabase;

public class EmbeddedNeo4jLinkStoreTest extends InJvmLinkStoreTest {

    @Override
    protected LinkStore createStore() {
        return new EmbeddedNeo4jGraphStore();
    }

    @Override
    protected void setUp() throws Exception {
        deleteDatabase();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        deleteDatabase();
    }
}
