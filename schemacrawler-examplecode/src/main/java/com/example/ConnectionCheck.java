package com.example;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import schemacrawler.crawl.ConnectionInfoBuilder;

public class ConnectionCheck {

  public static void main(final String[] args) throws SQLException {

    final String connectionUrl = "jdbc:hsqldb:hsql://localhost:9001/schemacrawler";

    final Connection connection = DriverManager.getConnection(connectionUrl);
    System.out.println(ConnectionInfoBuilder.builder(connection).buildDatabaseInfo());
    System.out.println(ConnectionInfoBuilder.builder(connection).buildJdbcDriverInfo());

    final DatabaseMetaData dbMetaData = connection.getMetaData();
    final ResultSet results = dbMetaData.getTables(null, null, "%", null);
    while (results.next()) {
      final String catalogName = results.getString("TABLE_CAT");
      final String schemaName = results.getString("TABLE_SCHEM");
      final String tableName = results.getString("TABLE_NAME");
      final String tableType = results.getString("TABLE_TYPE");
      System.out.printf("o--> %s//%s//%s (%s)%n", catalogName, schemaName, tableName, tableType);
    }
  }
}
