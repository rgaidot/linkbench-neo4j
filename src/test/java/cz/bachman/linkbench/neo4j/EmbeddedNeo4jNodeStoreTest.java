package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.store.NodeStore;

public class EmbeddedNeo4jNodeStoreTest extends InJvmNodeStoreTest {

    @Override
    protected NodeStore createStore() {
        return new EmbeddedNeo4jGraphStore();
    }
}
