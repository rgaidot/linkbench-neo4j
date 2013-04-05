package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.store.GraphStore;

public class EmbeddedNeo4jGraphStoreTest extends InJvmGraphStoreTest {

    @Override
    protected GraphStore createNewStore() {
        return new EmbeddedNeo4jGraphStore();
    }
}
