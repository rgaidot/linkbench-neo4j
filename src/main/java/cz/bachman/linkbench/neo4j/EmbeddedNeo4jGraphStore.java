package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.Phase;
import org.apache.commons.io.FileUtils;
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

    @Override
    public void initialize(Properties properties, Phase phase, int i) throws Exception {
        recreateDatabase();
    }

    @Override
    public void close() {
        db.shutdown();
    }

    @Override
    public void resetNodeStore(String dbid, long startID) throws Exception {
        super.resetNodeStore(dbid, startID);
        recreateDatabase();
    }

    private void recreateDatabase() throws IOException {
        FileUtils.deleteDirectory(new File(getStoreDir()));
        db = new EmbeddedGraphDatabase(getStoreDir(), loadProps());
    }

    private String getStoreDir() throws IOException {
        return loadProps().get("store_dir");
    }

    private Map<String, String> loadProps() throws IOException {
        return MapUtil.load(this.getClass().getClassLoader().getResourceAsStream("neo4j.properties"));
    }
}
