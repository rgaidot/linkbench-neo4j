package cz.bachman.linkbench.neo4j;

public class ImpermanentNeo4jGraphStoreFactoryTest extends InJvmGraphStoreFactoryTest {

    @Override
    protected InJvmGraphStoreFactory getFactory() {
        return new ImpermanentNeo4jGraphStoreFactory();
    }
}
