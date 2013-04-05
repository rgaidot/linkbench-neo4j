package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.Phase;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * {@link Neo4jGraphStore} using an {@link EmbeddedGraphDatabase}.
 */
public class EmbeddedNeo4jGraphStore extends Neo4jGraphStore {

    private static final Logger LOG = Logger.getLogger(EmbeddedNeo4jGraphStore.class);

    public EmbeddedNeo4jGraphStore() {
        recreateDatabase(true);
    }

    @Override
    public void initialize(Properties p, Phase currentPhase, int threadId) {
        recreateDatabase(false);
    }

    @Override
    public void resetNodeStore(String dbid, long startID) throws Exception {
        super.resetNodeStore(dbid, startID);
        recreateDatabase(true);
    }

    @Override
    public void close() {
        if (db != null) {
            db.shutdown();
        }
    }

    /**
     * Recreate the database.
     *
     * @param deleteOld whether to delete the files with data (true) or not (false).
     */
    private void recreateDatabase(boolean deleteOld) {
        close();

        if (deleteOld) {
            try {
                FileUtils.deleteDirectory(new File(getStoreDir()));
            } catch (IOException e) {
                LOG.error("Could not delete old database: " + e.getMessage());
            }
        }

        db = new EmbeddedGraphDatabase(getStoreDir(), loadProps());
        registerShutdownHook(db);
    }

    private String getStoreDir() {
        return loadProps().get("store_dir");
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
}
