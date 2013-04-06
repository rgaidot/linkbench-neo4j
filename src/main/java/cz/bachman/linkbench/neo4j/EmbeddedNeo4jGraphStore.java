package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.Phase;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * {@link Neo4jGraphStore} using an {@link EmbeddedGraphDatabase}.
 */
public class EmbeddedNeo4jGraphStore extends Neo4jGraphStore {

    private static final Logger LOG = Logger.getLogger(EmbeddedNeo4jGraphStore.class);
    private Properties properties;
    private final Set<Thread> users = new HashSet<>();

    @Override
    public synchronized void initialize(Properties properties, Phase currentPhase, int threadId) {
        if (db == null) {
            this.properties = properties;
            createDatabase();
        } else {
            LOG.info("Database already initialized, ignoring...");
            users.add(Thread.currentThread());
        }
    }

    @Override
    public synchronized void resetNodeStore(String dbid, long startID) throws Exception {
        LOG.info("Received request to reset node store");
        super.resetNodeStore(dbid, startID);
        recreateDatabase();
    }

    @Override
    public synchronized void close() {
        users.remove(Thread.currentThread());
        if (users.isEmpty()) {
            LOG.info("Shutting down database...");
            db.shutdown();
            db = null;
        } else {
            LOG.info("Not shutting database, still has " + users.size() + " users.");
        }
    }

    private void recreateDatabase() {
        LOG.info("Re-creating database...");
        deleteDatabase();
        createDatabase();
    }

    /**
     * Create the database.
     */
    private void createDatabase() {
        LOG.info("Creating database...");
        db = new EmbeddedGraphDatabase(getStoreDir(properties), propertiesToMap(properties));
        registerShutdownHook(db, properties);
        users.add(Thread.currentThread());
    }

    private void deleteDatabase() {
        LOG.info("Deleting database...");
        db.shutdown();
        deleteDatabaseFiles(properties);
        users.clear();
    }

    private static String getStoreDir(Properties properties) {
        return propertiesToMap(properties).get("store_dir");
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb, final Properties properties) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

    private static void deleteDatabaseFiles(Properties properties) {
        try {
            FileUtils.deleteDirectory(new File(getStoreDir(properties)));
        } catch (IOException e) {
            LOG.error("Could not delete old database: " + e.getMessage());
        }
    }
}
