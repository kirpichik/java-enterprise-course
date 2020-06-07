package org.polushin.java_enterprise.osm_jdbc.db.dao;

import org.polushin.java_enterprise.osm_jdbc.osm.Node;

import java.util.Collection;
import java.util.Optional;

public interface NodeDao {

    boolean insertUsingPrepareStatement(Node node);

    boolean insertUsingStatement(Node node);

    boolean insertBatch(Collection<Node> nodes);

    Optional<Node> getNodeById(int id);

    boolean updateNode(Node node);

    boolean deleteNodeById(int id);

}
