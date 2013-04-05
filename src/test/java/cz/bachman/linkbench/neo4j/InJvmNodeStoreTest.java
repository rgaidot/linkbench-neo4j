package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.*;
import com.facebook.LinkBench.store.NodeStore;
import com.facebook.LinkBench.store.NodeStoreFactory;

import java.util.Properties;

public abstract class InJvmNodeStoreTest extends NodeStoreTestBase {

    private NodeStore nodeStore;
    private Properties props;

    @Override
    protected void initNodeStore(Properties properties) throws Exception {
        this.props = properties;
        nodeStore = createStore();
    }

    protected abstract NodeStore createStore();

    @Override
    protected NodeStore getNodeStoreHandle(boolean initialize) throws Exception {
        if (initialize) {
            nodeStore.initialize(props, Phase.REQUEST, 0);
        }

        return nodeStore;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        nodeStore.close();
    }
}
