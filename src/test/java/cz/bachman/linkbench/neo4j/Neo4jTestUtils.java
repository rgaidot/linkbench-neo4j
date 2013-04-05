package cz.bachman.linkbench.neo4j;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Test utilities.
 */
public final class Neo4jTestUtils {

    private Neo4jTestUtils() {
    }

    public static void loadNeo4jProps(Properties properties) {
        try {
            properties.putAll(neo4jProps());
        } catch (IOException e) {
            System.out.println("Unable to load Neo4j properties");
            throw new IllegalStateException(e);
        }
    }

    public static void deleteDatabase() {
        try {
            FileUtils.deleteDirectory(new File(neo4jProps().getProperty("store_dir")));
        } catch (IOException e) {
            System.out.println("Could not delete old database: " + e.getMessage());
        }
    }

    private static Properties neo4jProps() throws IOException {
        Properties neoProps = new Properties();
        neoProps.load(Neo4jTestUtils.class.getClassLoader().getResourceAsStream("neo4j.properties"));
        return neoProps;
    }
}
