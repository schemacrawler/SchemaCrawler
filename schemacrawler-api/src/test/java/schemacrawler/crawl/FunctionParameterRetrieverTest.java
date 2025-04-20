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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.schema.ParameterModeType.in;
import static schemacrawler.schema.ParameterModeType.inOut;
import static schemacrawler.schema.ParameterModeType.out;
import static schemacrawler.schema.ParameterModeType.result;
import static schemacrawler.schema.ParameterModeType.unknown;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionParametersRetrievalStrategy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schema.FunctionParameter;
import schemacrawler.schema.ParameterModeType;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.utility.JavaSqlTypes;
import schemacrawler.utility.TypeMap;
import us.fatehi.test.utility.TestObjectUtility;

public class FunctionParameterRetrieverTest {

  private Connection connection;
  private MutableCatalog catalog;
  private FunctionParameterRetriever retriever;
  private NamedObjectList<MutableRoutine> allRoutines;

  @BeforeEach
  public void setUp() throws SQLException {

    System.setProperty("org.mockito.debug", "true");

    connection = TestObjectUtility.mockConnection();

    final RetrieverConnection retrieverConnection = mock(RetrieverConnection.class);
    when(retrieverConnection.getConnection()).thenReturn(connection);
    when(retrieverConnection.getJavaSqlTypes()).thenReturn(new JavaSqlTypes());
    when(retrieverConnection.getTypeMap()).thenReturn(new TypeMap());
    when(retrieverConnection.get(functionParametersRetrievalStrategy))
        .thenReturn(MetadataRetrievalStrategy.metadata);

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

    // Create retriever
    retriever = new FunctionParameterRetriever(retrieverConnection, catalog, options);

    // Initialize routines list
    allRoutines = new NamedObjectList<>();
  }

  @Test
  public void testParameterModeMapping() throws SQLException {

    final String functionName = "testFunction";
    final String paramName = "paramName";

    final Map<Integer, ParameterModeType> expectedModeMap = new HashMap<>();
    expectedModeMap.put(DatabaseMetaData.functionColumnIn, in);
    expectedModeMap.put(DatabaseMetaData.functionColumnInOut, inOut);
    expectedModeMap.put(DatabaseMetaData.functionColumnOut, out);
    expectedModeMap.put(DatabaseMetaData.functionColumnResult, result);
    expectedModeMap.put(99, unknown); // Some arbitrary value

    setupMockFunction(functionName);

    for (final Map.Entry<Integer, ParameterModeType> entry : expectedModeMap.entrySet()) {

      final ResultSet resultSet = setupFunctionsResultSet(functionName, paramName, entry.getKey());
      final DatabaseMetaData metaData = connection.getMetaData();
      when(metaData.getFunctionColumns(any(), any(), any(), any())).thenReturn(resultSet);

      retriever.retrieveFunctionParameters(allRoutines, new IncludeAll());

      final MutableFunction function = (MutableFunction) allRoutines.iterator().next();
      final Optional<MutableFunctionParameter> paramOptional = function.lookupParameter(paramName);

      assertThat(
          "Parameter with mode <" + entry.getKey() + "> should exist",
          paramOptional.isPresent(),
          is(true));

      if (paramOptional.isPresent()) {
        assertThat(
            "Parameter mode should match for type " + entry.getKey(),
            paramOptional.get().getParameterMode(),
            is(entry.getValue()));
      }
    }
  }

  @Test
  public void testRetrieveFunctionParameters() throws SQLException {

    final String functionName = "testFunction";
    final String paramName = "paramName";
    setupMockFunction(functionName);
    final ResultSet resultSet =
        setupFunctionsResultSet(functionName, paramName, DatabaseMetaData.functionColumnIn);
    final DatabaseMetaData metaData = connection.getMetaData();
    when(metaData.getFunctionColumns(any(), any(), any(), any())).thenReturn(resultSet);

    retriever.retrieveFunctionParameters(allRoutines, new IncludeAll());

    final MutableFunction function = (MutableFunction) allRoutines.iterator().next();
    assertThat(function.getParameters(), hasSize(1));

    final FunctionParameter parameter = function.getParameters().get(0);
    assertThat(parameter.getName(), is(paramName));
    assertThat(parameter.getParameterMode(), is(in));
    assertThat(parameter.getOrdinalPosition(), is(1));
  }

  @Test
  public void testRetrieveFunctionParametersWhenNoFunctions() throws SQLException {
    retriever.retrieveFunctionParameters(allRoutines, new IncludeAll());

    assertThat(allRoutines.size(), is(0));
  }

  private ResultSet setupFunctionsResultSet(
      final String functionName, final String paramName, final int columnType) throws SQLException {
    final String[] columnNames = {
      "FUNCTION_CAT",
      "FUNCTION_SCHEM",
      "FUNCTION_NAME",
      "COLUMN_NAME",
      "COLUMN_TYPE",
      "DATA_TYPE",
      "TYPE_NAME",
      "PRECISION",
      "LENGTH",
      "SCALE",
      "RADIX",
      "NULLABLE",
      "REMARKS",
      "CHAR_OCTET_LENGTH",
      "ORDINAL_POSITION",
      "IS_NULLABLE",
      "SPECIFIC_NAME"
    };

    final Object[][] data = {
      {
        null,
        null,
        functionName,
        paramName,
        columnType,
        12,
        "VARCHAR",
        255,
        255,
        0,
        10,
        DatabaseMetaData.functionNullable,
        "",
        255,
        1,
        "YES",
        "testFunction"
      }
    };

    final String resultSetDescription = String.format("Function parameters for <%s>", functionName);
    return TestObjectUtility.mockResultSet(resultSetDescription, columnNames, data);
  }

  private void setupMockFunction(final String functionName) {
    final MutableFunction function =
        new MutableFunction(new SchemaReference(null, null), functionName, functionName);
    allRoutines.add(function);
  }
}
