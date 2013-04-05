package cz.bachman.linkbench.neo4j;

/**
 * {@link InJvmGraphStoreFactory} for {@link EmbeddedNeo4jGraphStore}.
 */
public class EmbeddedNeo4jGraphStoreFactory extends InJvmGraphStoreFactory {

    @Override
    protected Neo4jGraphStore instantiateGraphStore() {
        return new EmbeddedNeo4jGraphStore();
    }
}
