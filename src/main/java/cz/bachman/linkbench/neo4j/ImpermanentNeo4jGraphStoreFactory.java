package cz.bachman.linkbench.neo4j;

/**
 * {@link InJvmGraphStoreFactory} for {@link ImpermanentNeo4jGraphStore}.
 */
public class ImpermanentNeo4jGraphStoreFactory extends InJvmGraphStoreFactory {

    @Override
    protected Neo4jGraphStore instantiateGraphStore() {
        return new ImpermanentNeo4jGraphStore();
    }
}
