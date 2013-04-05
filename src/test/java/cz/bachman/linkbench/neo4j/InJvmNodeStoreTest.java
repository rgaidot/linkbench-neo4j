package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.*;
import com.facebook.LinkBench.store.NodeStore;
import com.facebook.LinkBench.store.NodeStoreFactory;

import java.io.IOException;
import java.util.Properties;

import static cz.bachman.linkbench.neo4j.Neo4jTestUtils.loadNeo4jProps;

public abstract class InJvmNodeStoreTest extends NodeStoreTestBase {

    private NodeStore nodeStore;
    private Properties props;

    @Override
    protected Properties basicProps() {
        Properties properties = super.basicProps();
        loadNeo4jProps(properties);
        return properties;
    }

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
