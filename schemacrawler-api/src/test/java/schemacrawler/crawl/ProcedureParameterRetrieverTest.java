/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.procedureParametersRetrievalStrategy;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schema.ParameterModeType;
import schemacrawler.schema.ProcedureParameter;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import us.fatehi.test.utility.TestObjectUtility;

public class ProcedureParameterRetrieverTest extends AbstractParameterRetrieverTest {

  @Test
  public void testParameterModeMapping() throws SQLException {
    when(retrieverConnection.get(procedureParametersRetrievalStrategy))
        .thenReturn(MetadataRetrievalStrategy.metadata);

    final String procedureName = "testProcedure";
    final String paramName = "paramName";

    final Map<Integer, ParameterModeType> expectedModeMap = new HashMap<>();
    expectedModeMap.put(DatabaseMetaData.procedureColumnIn, in);
    expectedModeMap.put(DatabaseMetaData.procedureColumnInOut, inOut);
    expectedModeMap.put(DatabaseMetaData.procedureColumnOut, out);
    expectedModeMap.put(DatabaseMetaData.procedureColumnResult, result);
    expectedModeMap.put(DatabaseMetaData.procedureColumnReturn, returnValue);
    expectedModeMap.put(99, unknown); // Some arbitrary value

    setupMockRoutine(procedureName);

    for (final Map.Entry<Integer, ParameterModeType> entry : expectedModeMap.entrySet()) {
      final ResultSet resultSet = setupResultSet(procedureName, paramName, entry.getKey());
      configureMetaData(resultSet);

      ((ProcedureParameterRetriever) retriever)
          .retrieveProcedureParameters(allRoutines, new IncludeAll());

      final MutableProcedure procedure = (MutableProcedure) allRoutines.iterator().next();
      final Optional<MutableProcedureParameter> paramOptional =
          procedure.lookupParameter(paramName);

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
  public void testRetrieveProcedureParametersFromDataDictionary() throws SQLException {
    when(retrieverConnection.get(procedureParametersRetrievalStrategy))
        .thenReturn(MetadataRetrievalStrategy.data_dictionary_all);

    final String procedureName = "testProcedure";
    final String paramName = "paramName";

    setupMockRoutine(procedureName);
    final ResultSet resultSet =
        setupResultSet(procedureName, paramName, DatabaseMetaData.procedureColumnIn);
    configureMetaData(resultSet);

    ((ProcedureParameterRetriever) retriever)
        .retrieveProcedureParameters(allRoutines, new IncludeAll());

    final MutableProcedure procedure = (MutableProcedure) allRoutines.iterator().next();
    assertThat(procedure.getParameters(), hasSize(1));

    final ProcedureParameter parameter = procedure.getParameters().get(0);
    assertThat(parameter.getName(), is(paramName));
    assertThat(parameter.getParameterMode(), is(in));
    assertThat(parameter.getOrdinalPosition(), is(1));
  }

  @Test
  public void testRetrieveProcedureParametersFromMetaData() throws SQLException {
    when(retrieverConnection.get(procedureParametersRetrievalStrategy))
        .thenReturn(MetadataRetrievalStrategy.metadata);

    final String procedureName = "testProcedure";
    final String paramName = "paramName";

    setupMockRoutine(procedureName);
    final ResultSet resultSet =
        setupResultSet(procedureName, paramName, DatabaseMetaData.procedureColumnIn);
    configureMetaData(resultSet);

    ((ProcedureParameterRetriever) retriever)
        .retrieveProcedureParameters(allRoutines, new IncludeAll());

    final MutableProcedure procedure = (MutableProcedure) allRoutines.iterator().next();
    assertThat(procedure.getParameters(), hasSize(1));

    final ProcedureParameter parameter = procedure.getParameters().get(0);
    assertThat(parameter.getName(), is(paramName));
    assertThat(parameter.getParameterMode(), is(in));
    assertThat(parameter.getOrdinalPosition(), is(1));
  }

  @Test
  public void testRetrieveProcedureParametersWhenNoProcedures() throws SQLException {
    when(retrieverConnection.get(procedureParametersRetrievalStrategy))
        .thenReturn(MetadataRetrievalStrategy.metadata);

    ((ProcedureParameterRetriever) retriever)
        .retrieveProcedureParameters(allRoutines, new IncludeAll());

    assertThat(allRoutines.size(), is(0));
  }

  @Override
  protected void configureMetaData(final ResultSet resultSet) throws SQLException {
    final DatabaseMetaData metaData = connection.getMetaData();
    when(metaData.getProcedureColumns(any(), any(), any(), any())).thenReturn(resultSet);
    when(connection.createStatement().getResultSet()).thenReturn(resultSet);
  }

  @Override
  protected void createRetriever(final SchemaCrawlerOptions options) {
    retriever = new ProcedureParameterRetriever(retrieverConnection, catalog, options);
  }

  @Override
  protected void setupMockRoutine(final String routineName) {
    final MutableProcedure procedure =
        new MutableProcedure(new SchemaReference(null, null), routineName, routineName);
    allRoutines.add(procedure);
  }

  @Override
  protected ResultSet setupResultSet(
      final String routineName, final String paramName, final int columnType) throws SQLException {
    final String[] columnNames = {
      "PROCEDURE_CAT",
      "PROCEDURE_SCHEM",
      "PROCEDURE_NAME",
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
      "COLUMN_DEF",
      "SQL_DATA_TYPE",
      "SQL_DATETIME_SUB",
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
        DatabaseMetaData.procedureNullable,
        "",
        null,
        null,
        null,
        255,
        1,
        "YES",
        "testProcedure"
      }
    };

    final String resultSetDescription = String.format("Procedure parameters for <%s>", routineName);
    return TestObjectUtility.mockResultSet(resultSetDescription, columnNames, data);
  }
}
