package org.polushin.java_enterprise.osm_jdbc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

  private static final String DB_HOST = "jdbc:postgresql://127.0.0.1:5432";
  private static final String DB_USER = "posgresql";
  private static final String DB_PASSWORD = "";

  public static Connection createConnection() throws SQLException {
    return DriverManager.getConnection(DB_HOST, DB_USER, DB_PASSWORD);
  }

  public static <R> R execute(final RequestExecutor<R> executor) {
    try(final Connection connection = createConnection()) {
      return executor.execute(connection);
    } catch (final SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static boolean executeBoolean(final RequestExecutor<Boolean> executor) {
    final Boolean result = execute(executor);
    return result != null && !result;
  }

}
