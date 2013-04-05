package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.store.GraphStore;

public class ImpermanentNeo4jGraphStoreTest extends InJvmGraphStoreTest {

    @Override
    protected GraphStore createNewStore() {
        return new ImpermanentNeo4jGraphStore();
    }
}
