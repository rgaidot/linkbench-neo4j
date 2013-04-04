package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.DummyLinkStore;
import com.facebook.LinkBench.GraphStore;
import com.facebook.LinkBench.GraphStoreTestBase;
import com.facebook.LinkBench.Phase;

import java.util.Properties;

public class EmbeddedNeo4jGraphStoreTest extends GraphStoreTestBase {

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
        wrappedStore = new EmbeddedNeo4jGraphStore();
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
