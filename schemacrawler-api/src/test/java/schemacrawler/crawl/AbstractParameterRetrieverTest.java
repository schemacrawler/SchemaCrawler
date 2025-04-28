/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.crawl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.utility.JavaSqlTypes;
import schemacrawler.utility.TypeMap;
import us.fatehi.test.utility.TestObjectUtility;

/** An abstract base class for parameter retriever tests to reduce code duplication. */
public abstract class AbstractParameterRetrieverTest {

  protected Connection connection;
  protected MutableCatalog catalog;
  protected AbstractRetriever retriever;
  protected NamedObjectList<MutableRoutine> allRoutines;
  protected RetrieverConnection retrieverConnection;

  @BeforeEach
  public void setUpBase() throws SQLException {
    System.setProperty("org.mockito.debug", "true");

    connection = TestObjectUtility.mockConnection();

    retrieverConnection = mock(RetrieverConnection.class);
    when(retrieverConnection.getConnection()).thenReturn(connection);
    when(retrieverConnection.getJavaSqlTypes()).thenReturn(new JavaSqlTypes());
    when(retrieverConnection.getTypeMap()).thenReturn(new TypeMap());

    // Setup IdentifierQuotingStrategy
    final Identifiers identifiers = mock(Identifiers.class);
    when(retrieverConnection.getIdentifiers()).thenReturn(identifiers);

    // Setup Catalog and Options
    catalog =
        new MutableCatalog(
            "testCatalog",
            new MutableDatabaseInfo("Test Database", "0.1", "SA"),
            new MutableJdbcDriverInfo(
                "com.example.Driver", "com.example.Driver", "0.1", 0, 0, 0, 0, false, "jdbc:test"));
    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    // Setup InformationSchemaViews
    final InformationSchemaViews informationSchemaViews = mock(InformationSchemaViews.class);
    final Query query = mock(Query.class);
    when(informationSchemaViews.hasQuery(any())).thenReturn(true);
    when(informationSchemaViews.getQuery(any())).thenReturn(query);
    when(retrieverConnection.getInformationSchemaViews()).thenReturn(informationSchemaViews);

    // Create specific retriever in subclass
    createRetriever(options);

    // Initialize routines list
    allRoutines = new NamedObjectList<>();
  }

  /**
   * Configure the database metadata to return the given result set.
   *
   * @param resultSet The result set to return
   * @throws SQLException If a database access error occurs
   */
  protected abstract void configureMetaData(ResultSet resultSet) throws SQLException;

  /**
   * Create the appropriate retriever for the test.
   *
   * @param options SchemaCrawlerOptions to use
   */
  protected abstract void createRetriever(SchemaCrawlerOptions options);

  /**
   * Creates a mock routine of the appropriate type.
   *
   * @param routineName Name of the routine to create
   */
  protected abstract void setupMockRoutine(String routineName);

  /**
   * Set up a result set for the routine parameters.
   *
   * @param routineName Name of the routine
   * @param paramName Name of the parameter
   * @param columnType Column type for the parameter
   * @return A mocked ResultSet
   */
  protected abstract ResultSet setupResultSet(String routineName, String paramName, int columnType)
      throws SQLException;
}
