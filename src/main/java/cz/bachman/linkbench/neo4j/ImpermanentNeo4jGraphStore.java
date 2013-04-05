package cz.bachman.linkbench.neo4j;

import com.facebook.LinkBench.Phase;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.io.IOException;
import java.util.Properties;

/**
 * {@link Neo4jGraphStore} using an {@link ImpermanentGraphDatabase}. Mostly intended for testing.
 */
public class ImpermanentNeo4jGraphStore extends Neo4jGraphStore {

    ImpermanentNeo4jGraphStore() {
        db = new ImpermanentGraphDatabase(loadProps());
    }

    @Override
    public void initialize(Properties p, Phase currentPhase, int threadId) throws Exception {
        //Do nothing! This is an impermanent graph db, so re-creating it would lead to all data being lost!
    }

    @Override
    public void close() {
        //Do nothing! This is an impermanent graph db, so after closing it, you can't get your data back.
    }

    @Override
    public void resetNodeStore(String dbid, long startID) throws Exception {
        super.resetNodeStore(dbid, startID);
        ((ImpermanentGraphDatabase) db).cleanContent();
    }
}
