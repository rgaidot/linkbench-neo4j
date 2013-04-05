package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.store.*;

/**
 * {@link NodeStoreFactory} and {@link LinkStoreFactory} for in-JVM graph stores.
 * <p/>
 * It creates the store only once and always returns the same instance.
 */
public abstract class InJvmGraphStoreFactory implements NodeStoreFactory, LinkStoreFactory {

    private static GraphStore STORE;

    @Override
    public synchronized LinkStore createLinkStore() {
        return getOrCreateStore();
    }

    @Override
    public synchronized NodeStore createNodeStore(LinkStore linkStore) {
        if (linkStore != null && linkStore != STORE) {
            throw new IllegalArgumentException("Illegal LinkStore passed into the factory.");
        }

        return getOrCreateStore();
    }

    private GraphStore getOrCreateStore() {
        if (STORE == null) {
            STORE = instantiateGraphStore();
        }

        return STORE;
    }

    protected abstract Neo4jGraphStore instantiateGraphStore();
}
