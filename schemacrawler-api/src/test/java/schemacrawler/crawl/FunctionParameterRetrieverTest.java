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
import static org.mockito.Mockito.when;
import static schemacrawler.schema.ParameterModeType.in;
import static schemacrawler.schema.ParameterModeType.inOut;
import static schemacrawler.schema.ParameterModeType.out;
import static schemacrawler.schema.ParameterModeType.result;
import static schemacrawler.schema.ParameterModeType.returnValue;
import static schemacrawler.schema.ParameterModeType.unknown;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionParametersRetrievalStrategy;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schema.FunctionParameter;
import schemacrawler.schema.ParameterModeType;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import us.fatehi.test.utility.TestObjectUtility;

public class FunctionParameterRetrieverTest extends AbstractParameterRetrieverTest {

  @Test
  public void testParameterModeMapping() throws SQLException {
    final String functionName = "testFunction";
    final String paramName = "paramName";

    final Map<Integer, ParameterModeType> expectedModeMap = new HashMap<>();
    expectedModeMap.put(DatabaseMetaData.functionColumnIn, in);
    expectedModeMap.put(DatabaseMetaData.functionColumnInOut, inOut);
    expectedModeMap.put(DatabaseMetaData.functionColumnOut, out);
    expectedModeMap.put(DatabaseMetaData.functionColumnResult, result);
    expectedModeMap.put(DatabaseMetaData.functionReturn, returnValue);
    expectedModeMap.put(99, unknown); // Some arbitrary value

    setupMockRoutine(functionName);

    for (final Map.Entry<Integer, ParameterModeType> entry : expectedModeMap.entrySet()) {
      final ResultSet resultSet = setupResultSet(functionName, paramName, entry.getKey());
      configureMetaData(resultSet);

      ((FunctionParameterRetriever) retriever)
          .retrieveFunctionParameters(allRoutines, new IncludeAll());

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

    setupMockRoutine(functionName);
    final ResultSet resultSet =
        setupResultSet(functionName, paramName, DatabaseMetaData.functionColumnIn);
    configureMetaData(resultSet);

    ((FunctionParameterRetriever) retriever)
        .retrieveFunctionParameters(allRoutines, new IncludeAll());

    final MutableFunction function = (MutableFunction) allRoutines.iterator().next();
    assertThat(function.getParameters(), hasSize(1));

    final FunctionParameter parameter = function.getParameters().get(0);
    assertThat(parameter.getName(), is(paramName));
    assertThat(parameter.getParameterMode(), is(in));
    assertThat(parameter.getOrdinalPosition(), is(1));
  }

  @Test
  public void testRetrieveFunctionParametersWhenNoFunctions() throws SQLException {
    ((FunctionParameterRetriever) retriever)
        .retrieveFunctionParameters(allRoutines, new IncludeAll());

    assertThat(allRoutines.size(), is(0));
  }

  @Override
  protected void configureMetaData(final ResultSet resultSet) throws SQLException {
    final DatabaseMetaData metaData = connection.getMetaData();
    when(metaData.getFunctionColumns(any(), any(), any(), any())).thenReturn(resultSet);
    when(connection.createStatement().getResultSet()).thenReturn(resultSet);
  }

  @Override
  protected void configureMetadataRetrievalStrategy() {
    when(retrieverConnection.get(functionParametersRetrievalStrategy))
        .thenReturn(MetadataRetrievalStrategy.metadata);
  }

  @Override
  protected void createRetriever(final SchemaCrawlerOptions options) {
    retriever = new FunctionParameterRetriever(retrieverConnection, catalog, options);
  }

  @Override
  protected void setupMockRoutine(final String routineName) {
    final MutableFunction function =
        new MutableFunction(new SchemaReference(null, null), routineName, routineName);
    allRoutines.add(function);
  }

  @Override
  protected ResultSet setupResultSet(
      final String routineName, final String paramName, final int columnType) throws SQLException {
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
        routineName,
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

    final String resultSetDescription = String.format("Function parameters for <%s>", routineName);
    return TestObjectUtility.mockResultSet(resultSetDescription, columnNames, data);
  }
}
