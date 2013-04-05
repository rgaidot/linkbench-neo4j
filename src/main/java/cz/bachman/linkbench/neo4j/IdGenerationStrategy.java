package cz.bachman.linkbench.neo4j;

/**
 * Strategy for generating Neo4j node IDs. Please not that these IDs are stores as properties on nodes,
 * we're not talking about the internal IDs!
 */
public enum IdGenerationStrategy {

    /**
     * Never generate own ID, use whatever is provided from {@link com.facebook.LinkBench.Node}.
     */
    GENERATE_NEVER,

    /**
     * Generate own ID if {@link com.facebook.LinkBench.Node} is -1.
     */
    GENERATE_IF_MINUS_ONE,

    /**
     * Always generate own ID, ignore whatever is in {@link com.facebook.LinkBench.Node}.
     */
    GENERATE_ALWAYS
}
