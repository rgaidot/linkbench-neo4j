package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.*;

import java.util.Properties;

public class ImpermanentNeo4jNodeStoreTest extends NodeStoreTestBase {

    private NodeStore nodeStore;
    private Properties props;

    @Override
    protected void initNodeStore(Properties properties) throws Exception {
        this.props = properties;
        nodeStore = new ImpermanentNeo4jGraphStore();
    }

    @Override
    protected NodeStore getNodeStoreHandle(boolean initialize) throws Exception {
        if (initialize) {
            nodeStore.initialize(props, Phase.REQUEST, 0);
        }

        return nodeStore;
    }
}
