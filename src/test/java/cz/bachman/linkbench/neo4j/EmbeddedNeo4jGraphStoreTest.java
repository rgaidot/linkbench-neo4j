package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.store.GraphStore;

import static cz.bachman.linkbench.neo4j.Neo4jTestUtils.*;

public class EmbeddedNeo4jGraphStoreTest extends InJvmGraphStoreTest {

    @Override
    protected GraphStore createNewStore() {
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
