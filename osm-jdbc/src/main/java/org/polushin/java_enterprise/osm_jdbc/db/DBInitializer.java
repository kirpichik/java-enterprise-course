package org.polushin.java_enterprise.osm_jdbc.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public final class DBInitializer {

  private static final String INIT_SCRIPT = "db_scheme_init.sql";

  public static void initDb(final Connection connection) throws SQLException, IOException {
    try (final var statement = connection.createStatement()) {
      statement.execute(getScriptFromResource(INIT_SCRIPT));
    }
  }

  private static String getScriptFromResource(final String resource) throws IOException {
    return getScriptFromStream(DBInitializer.class.getClassLoader().getResourceAsStream(resource));
  }

  private static String getScriptFromStream(final InputStream stream) throws IOException {
    final var reader = new BufferedReader(new InputStreamReader(stream));
    final var sql = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) sql.append(line).append("\n");
    return sql.toString();
  }

}
