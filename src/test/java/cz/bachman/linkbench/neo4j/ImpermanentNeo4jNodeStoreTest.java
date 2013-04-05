package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.store.NodeStore;

public class ImpermanentNeo4jNodeStoreTest extends InJvmNodeStoreTest {

    @Override
    protected NodeStore createStore() {
        return new ImpermanentNeo4jGraphStore();
    }
}
