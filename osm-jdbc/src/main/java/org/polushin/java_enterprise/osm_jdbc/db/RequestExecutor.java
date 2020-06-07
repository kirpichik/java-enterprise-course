package org.polushin.java_enterprise.osm_jdbc.db;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface RequestExecutor<R> {

    R execute(Connection connection) throws SQLException;

}
