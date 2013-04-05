package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.Phase;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * {@link Neo4jGraphStore} using an {@link EmbeddedGraphDatabase}.
 */
public class EmbeddedNeo4jGraphStore extends Neo4jGraphStore {

    private static final Logger LOG = Logger.getLogger(EmbeddedNeo4jGraphStore.class);
    private Properties properties;

    @Override
    public void initialize(Properties properties, Phase currentPhase, int threadId) {
        if (db == null) {
            this.properties = properties;
            createDatabase();
        }
        else {
            LOG.warn("Database already initialized, ignoring...");
        }
    }

    @Override
    public void resetNodeStore(String dbid, long startID) throws Exception {
        super.resetNodeStore(dbid, startID);
        recreateDatabase();
    }

    @Override
    public void close() {
        if (db != null) {
            db.shutdown();
            db = null;
        }
    }

    private void recreateDatabase() {
        deleteDatabase();
        createDatabase();
    }

    /**
     * Create the database.
     */
    private void createDatabase() {
        db = new EmbeddedGraphDatabase(getStoreDir(properties), propertiesToMap(properties));
        registerShutdownHook(db);
    }

    private void deleteDatabase() {
        close();
        try {
            FileUtils.deleteDirectory(new File(getStoreDir(properties)));
        } catch (IOException e) {
            LOG.error("Could not delete old database: " + e.getMessage());
        }
    }

    private String getStoreDir(Properties properties) {
        return propertiesToMap(properties).get("store_dir");
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
