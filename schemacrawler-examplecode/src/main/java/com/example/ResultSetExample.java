package com.example;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.SingleUseUserCredentials;
import schemacrawler.utility.SchemaCrawlerUtility;

public final class ResultSetExample
{

  private static Connection getConnection()
  {
    final String connectionUrl =
      "jdbc:hsqldb:hsql://localhost:9001/schemacrawler";
    final DatabaseConnectionSource dataSource =
      new DatabaseConnectionSource(connectionUrl);
    dataSource.setUserCredentials(new SingleUseUserCredentials("sa", ""));
    return dataSource.get();
  }

  public static void main(final String[] args)
    throws Exception
  {

    final String query =
      "SELECT                                                      \n"
      + "  BOOKS.TITLE AS BOOKTITLE,                               \n"
      + "  AUTHORS.FIRSTNAME + ' ' + AUTHORS.FIRSTNAME AS AUTHOR   \n"
      + "FROM                                                      \n"
      + "  PUBLIC.BOOKS.BOOKS AS BOOKS                             \n"
      + "  INNER JOIN PUBLIC.BOOKS.BOOKAUTHORS AS BOOKAUTHORS      \n"
      + "    ON BOOKS.ID = BOOKAUTHORS.BOOKID                      \n"
      + "  INNER JOIN PUBLIC.BOOKS.AUTHORS AS AUTHORS              \n"
      + "    ON BOOKAUTHORS.AUTHORID = AUTHORS.ID                  \n";
    try (
      final Connection connection = getConnection();
      final Statement statement = connection.createStatement();
      final ResultSet results = statement.executeQuery(query)
    )
    {
      // Get result set metadata
      final ResultsColumns resultColumns =
        SchemaCrawlerUtility.getResultsColumns(results);
      for (final ResultsColumn column : resultColumns)
      {
        System.out.println("o--> " + column);
        System.out.println("     - label:     " + column.getLabel());
        System.out.println("     - data-type: " + column.getColumnDataType());
        System.out.println("     - table:     " + column.getParent());
      }
    }

  }

}
