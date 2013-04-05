package cz.bachman.linkbench.neo4j;


import com.facebook.LinkBench.store.LinkStore;
import com.facebook.LinkBench.store.NodeStore;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;

public abstract class InJvmGraphStoreFactoryTest {

    @Test
    public void whenLinkStoreRequestedTwiceTheSameInstanceShouldBeReturned() {
        InJvmGraphStoreFactory factory = getFactory();

        LinkStore store1 = factory.createLinkStore();
        LinkStore store2 = factory.createLinkStore();

        assertNotNull(store1);
        assertTrue(store1 == store2);
    }

    @Test
    public void whenNodeStoreRequestedTwiceTheSameInstanceShouldBeReturned() {
        InJvmGraphStoreFactory factory = getFactory();

        NodeStore store1 = factory.createNodeStore(null);
        NodeStore store2 = factory.createNodeStore(null);

        assertNotNull(store1);
        assertTrue(store1 == store2);
    }

    @Test
    public void whenNodeStoreCreatedFromLinkStoreTheSameInstanceShouldBeReturned() {
        InJvmGraphStoreFactory factory = getFactory();

        LinkStore linkStore = factory.createLinkStore();
        NodeStore nodeStore = factory.createNodeStore(linkStore);

        assertTrue(linkStore == nodeStore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenWrongLinkStoreIsUsedAnExceptionIsThrown() {
        InJvmGraphStoreFactory factory = getFactory();

        factory.createLinkStore();
        factory.createNodeStore(new ImpermanentNeo4jGraphStore());
    }

    protected abstract InJvmGraphStoreFactory getFactory();
}
