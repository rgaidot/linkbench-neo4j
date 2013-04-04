package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.*;

import java.io.IOException;
import java.util.Properties;

public class ImpermanentNeo4jGraphStoreTest extends GraphStoreTestBase {

    private GraphStore wrappedStore;
    private Properties props;

    @Override
    protected long getIDCount() {
        return 500;
    }

    @Override
    protected int getRequestCount() {
        return 10000;
    }

    @Override
    protected void initStore(Properties properties) throws Exception {
        this.props = properties;
        wrappedStore = new ImpermanentNeo4jGraphStore();
    }

    @Override
    protected DummyLinkStore getStoreHandle(boolean initialize) throws Exception {
        DummyLinkStore store = new DummyLinkStore(wrappedStore);

        if (initialize) {
            store.initialize(props, Phase.REQUEST, 0);
        }

        return store;
    }
}
