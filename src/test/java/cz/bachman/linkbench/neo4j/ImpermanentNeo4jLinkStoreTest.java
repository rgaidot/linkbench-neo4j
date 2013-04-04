package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.DummyLinkStore;
import com.facebook.LinkBench.LinkStore;
import com.facebook.LinkBench.LinkStoreTestBase;
import com.facebook.LinkBench.Phase;

import java.io.IOException;
import java.util.Properties;

public class ImpermanentNeo4jLinkStoreTest extends LinkStoreTestBase {

    private LinkStore wrappedStore;
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
