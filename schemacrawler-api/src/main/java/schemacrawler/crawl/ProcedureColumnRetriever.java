/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.ProcedureParameter;
import schemacrawler.schema.ParameterModeType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.*;
import schemacrawler.utility.Query;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * A retriever uses database metadata to get the details about the
 * database procedure columns.
 *
 * @author Sualeh Fatehi
 */
final class ProcedureColumnRetriever
  extends AbstractRetriever
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ProcedureColumnRetriever.class.getName());

  ProcedureColumnRetriever(final RetrieverConnection retrieverConnection,
                           final MutableCatalog catalog,
                           final SchemaCrawlerOptions options)
    throws SQLException
  {
    super(retrieverConnection, catalog, options);
  }

  void retrieveProcedureColumns(final NamedObjectList<MutableRoutine> allRoutines,
                                final InclusionRule columnInclusionRule)
    throws SQLException
  {
    requireNonNull(allRoutines, "No procedures provided");

    final InclusionRuleFilter<ProcedureParameter> columnFilter = new InclusionRuleFilter<>(columnInclusionRule,
                                                                                           true);
    if (columnFilter.isExcludeAll())
    {
      LOGGER
        .log(Level.INFO,
             "Not retrieving procedure columns, since this was not requested");
      return;
    }

    final MetadataRetrievalStrategy procedureColumnRetrievalStrategy = getRetrieverConnection()
      .getProcedureColumnRetrievalStrategy();
    switch (procedureColumnRetrievalStrategy)
    {
      case data_dictionary_all:
        LOGGER
          .log(Level.INFO,
               "Retrieving procedure columns, using fast data dictionary retrieval");
        retrieveProcedureColumnsFromDataDictionary(allRoutines, columnFilter);
        break;

      case metadata_all:
        LOGGER
          .log(Level.INFO,
               "Retrieving procedure columns, using fast meta-data retrieval");
        retrieveProcedureColumnsFromMetadataForAllProcedures(allRoutines,
                                                             columnFilter);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving procedure columns");
        retrieveProcedureColumnsFromMetadata(allRoutines, columnFilter);
        break;

      default:
        break;
    }

  }

  private void createProcedureColumn(final MetadataResultSet results,
                                     final NamedObjectList<MutableRoutine> allRoutines,
                                     final InclusionRuleFilter<ProcedureParameter> columnFilter)
  {
    final String columnCatalogName = normalizeCatalogName(results
      .getString("PROCEDURE_CAT"));
    final String schemaName = normalizeSchemaName(results
      .getString("PROCEDURE_SCHEM"));
    final String procedureName = results.getString("PROCEDURE_NAME");
    final String columnName = results.getString("COLUMN_NAME");
    final String specificName = results.getString("SPECIFIC_NAME");

    LOGGER.log(Level.FINE,
               new StringFormat("Retrieving procedure column <%s.%s.%s.%s.%s>",
                                columnCatalogName,
                                schemaName,
                                procedureName,
                                specificName,
                                columnName));
    if (isBlank(columnName))
    {
      return;
    }

    final Optional<MutableRoutine> optionalRoutine = allRoutines.lookup(Arrays
      .asList(columnCatalogName, schemaName, procedureName, specificName));
    if (!optionalRoutine.isPresent())
    {
      return;
    }

    final MutableRoutine routine = optionalRoutine.get();
    if (routine.getRoutineType() != RoutineType.procedure)
    {
      return;
    }

    final MutableProcedure procedure = (MutableProcedure) routine;
    final MutableProcedureParameter column = lookupOrCreateProcedureColumn(procedure,
                                                                           columnName);
    if (columnFilter.test(column)
        && belongsToSchema(procedure, columnCatalogName, schemaName))
    {
      final ParameterModeType parameterMode = getProcedureParameterMode(results
        .getInt("COLUMN_TYPE", DatabaseMetaData.procedureColumnUnknown));
      final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
      final int dataType = results.getInt("DATA_TYPE", 0);
      final String typeName = results.getString("TYPE_NAME");
      final int length = results.getInt("LENGTH", 0);
      final int precision = results.getInt("PRECISION", 0);
      final boolean isNullable = results
        .getShort("NULLABLE",
                  (short) DatabaseMetaData.procedureNullableUnknown) == (short) DatabaseMetaData.procedureNullable;
      final String remarks = results.getString("REMARKS");
      column.setOrdinalPosition(ordinalPosition);
      column.setParameterMode(parameterMode);
      column
        .setColumnDataType(lookupOrCreateColumnDataType(procedure.getSchema(),
                                                        dataType,
                                                        typeName));
      column.setSize(length);
      column.setPrecision(precision);
      column.setNullable(isNullable);
      column.setRemarks(remarks);

      column.addAttributes(results.getAttributes());

      LOGGER.log(Level.FINER,
                 new StringFormat("Adding column to procedure <%s>",
                                  column.getFullName()));
      procedure.addColumn(column);
    }

  }

  private ParameterModeType getProcedureParameterMode(final int columnType)
  {
    switch (columnType)
    {
      case DatabaseMetaData.procedureColumnIn:
        return ParameterModeType.in;
      case DatabaseMetaData.procedureColumnInOut:
        return ParameterModeType.inOut;
      case DatabaseMetaData.procedureColumnOut:
        return ParameterModeType.out;
      case DatabaseMetaData.procedureColumnResult:
        return ParameterModeType.result;
      case DatabaseMetaData.procedureColumnReturn:
        return ParameterModeType.returnValue;
      default:
        return ParameterModeType.unknown;
    }
  }

  private MutableProcedureParameter lookupOrCreateProcedureColumn(final MutableProcedure procedure,
                                                                  final String columnName)
  {
    final Optional<MutableProcedureParameter> columnOptional = procedure
      .lookupColumn(columnName);
    final MutableProcedureParameter column;
    if (columnOptional.isPresent())
    {
      column = columnOptional.get();
    }
    else
    {
      column = new MutableProcedureParameter(procedure, columnName);
    }
    return column;
  }

  private void retrieveProcedureColumnsFromDataDictionary(final NamedObjectList<MutableRoutine> allRoutines,
                                                          final InclusionRuleFilter<ProcedureParameter> columnFilter)
    throws SQLException
  {
    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews
      .hasQuery(InformationSchemaKey.PROCEDURE_COLUMNS))
    {
      throw new SchemaCrawlerSQLException("No procedure columns SQL provided",
                                          null);
    }
    final Query procedureColumnsSql = informationSchemaViews
      .getQuery(InformationSchemaKey.PROCEDURE_COLUMNS);
    final Connection connection = getDatabaseConnection();
    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet(procedureColumnsSql,
                                                                statement,
                                                                getSchemaInclusionRule());)
    {
      results.setDescription("retrieveProcedureColumnsFromDataDictionary");
      while (results.next())
      {
        createProcedureColumn(results, allRoutines, columnFilter);
      }
    }
  }

  private void retrieveProcedureColumnsFromMetadata(final NamedObjectList<MutableRoutine> allRoutines,
                                                    final InclusionRuleFilter<ProcedureParameter> columnFilter)
    throws SchemaCrawlerSQLException
  {
    for (final MutableRoutine routine: allRoutines)
    {
      if (routine.getRoutineType() != RoutineType.procedure)
      {
        continue;
      }

      final MutableProcedure procedure = (MutableProcedure) routine;
      LOGGER.log(Level.FINE, "Retrieving procedure columns for " + procedure);
      try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
        .getProcedureColumns(procedure.getSchema().getCatalogName(),
                             procedure.getSchema().getName(),
                             procedure.getName(),
                             null));)
      {
        while (results.next())
        {
          createProcedureColumn(results, allRoutines, columnFilter);
        }
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerSQLException(String
          .format("Could not retrieve procedure columns for procedure <%s>",
                  procedure), e);
      }
    }
  }

  private void retrieveProcedureColumnsFromMetadataForAllProcedures(final NamedObjectList<MutableRoutine> allRoutines,
                                                                    final InclusionRuleFilter<ProcedureParameter> columnFilter)
    throws SQLException
  {
    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getProcedureColumns(null, null, "%", "%"));)
    {
      while (results.next())
      {
        createProcedureColumn(results, allRoutines, columnFilter);
      }
    }
  }

}
