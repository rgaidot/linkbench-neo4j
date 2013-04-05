package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.DummyLinkStore;
import com.facebook.LinkBench.GraphStoreTestBase;
import com.facebook.LinkBench.Phase;
import com.facebook.LinkBench.store.GraphStore;

import java.util.Properties;

import static cz.bachman.linkbench.neo4j.Neo4jTestUtils.loadNeo4jProps;

public abstract class InJvmGraphStoreTest extends GraphStoreTestBase {

    private GraphStore graphStore;
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
    protected Properties basicProps() {
        Properties properties = super.basicProps();
        loadNeo4jProps(properties);
        return properties;
    }

    @Override
    protected void initStore(Properties properties) throws Exception {
        this.props = properties;
        graphStore = createNewStore();
    }

    protected abstract GraphStore createNewStore();

    @Override
    protected DummyLinkStore getStoreHandle(boolean initialize) throws Exception {
        DummyLinkStore store = new DummyLinkStore(graphStore);

        if (initialize) {
            store.initialize(props, Phase.REQUEST, 0);
        }

        return store;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        graphStore.close();
    }
}
