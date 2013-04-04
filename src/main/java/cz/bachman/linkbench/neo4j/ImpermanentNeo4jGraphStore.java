package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.Phase;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.io.IOException;
import java.util.Properties;

/**
 * {@link Neo4jGraphStore} using an {@link ImpermanentGraphDatabase}. Mostly intended for testing.
 */
public class ImpermanentNeo4jGraphStore extends Neo4jGraphStore {

    public ImpermanentNeo4jGraphStore() {
        db = new ImpermanentGraphDatabase(MapUtil.stringMap(
                "all_stores_total_mapped_memory_size", "3G",
                "dump_configuration", "true",
                "keep_logical_logs", "false",
                "log_mapped_memory_stats", "true",
                "log_mapped_memory_stats_interval", "100000",
                "neostore.nodestore.db.mapped_memory", "500M",
                "neostore.propertystore.db.arrays.mapped_memory", "500M",
                "neostore.propertystore.db.index.keys.mapped_memory", "10M",
                "neostore.propertystore.db.index.mapped_memory", "500M",
                "neostore.propertystore.db.mapped_memory", "500M",
                "neostore.propertystore.db.strings.mapped_memory", "20M",
                "neostore.relationshipstore.db.mapped_memory", "500M"
        ));
    }

    @Override
    public void initialize(Properties p, Phase currentPhase, int threadId) throws Exception {
        //Do nothing! Especially don't create a new store, as you would lose all the data,
        //since ImpermanentGraphDatabase is used.
    }

    @Override
    public void resetNodeStore(String dbid, long startID) throws Exception {
        super.resetNodeStore(dbid, startID);
        ((ImpermanentGraphDatabase) db).cleanContent();
    }

    @Override
    public void close() {
        //prevent from being closed, ImpermanentGraphDatabase can't be re-opened again.
    }
}
