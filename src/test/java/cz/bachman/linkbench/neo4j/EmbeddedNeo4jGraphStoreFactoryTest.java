package cz.bachman.linkbench.neo4j;

public class EmbeddedNeo4jGraphStoreFactoryTest extends InJvmGraphStoreFactoryTest {

    @Override
    protected InJvmGraphStoreFactory getFactory() {
        return new EmbeddedNeo4jGraphStoreFactory();
    }
}
