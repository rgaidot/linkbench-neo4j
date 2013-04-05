package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.store.LinkStore;

public class ImpermanentNeo4jLinkStoreTest extends InJvmLinkStoreTest {

    @Override
    protected LinkStore createStore() {
        return new ImpermanentNeo4jGraphStore();
    }
}
