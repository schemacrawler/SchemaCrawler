/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

import static sf.util.Utility.isBlank;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.Function;
import schemacrawler.schema.FunctionColumn;
import schemacrawler.schema.FunctionColumnType;
import schemacrawler.schema.FunctionReturnType;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.ProcedureColumnType;
import schemacrawler.schema.ProcedureReturnType;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import sf.util.StringFormat;

/**
 * A retriever uses database metadata to get the details about the
 * database procedures.
 *
 * @author Sualeh Fatehi
 */
final class RoutineRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(RoutineRetriever.class.getName());

  RoutineRetriever(final RetrieverConnection retrieverConnection,
                   final MutableCatalog catalog,
                   final SchemaCrawlerOptions options)
    throws SQLException
  {
    super(retrieverConnection, catalog, options);
  }

  void retrieveFunctionColumns(final MutableFunction function,
                               final InclusionRule columnInclusionRule)
    throws SQLException
  {
    final InclusionRuleFilter<FunctionColumn> columnFilter = new InclusionRuleFilter<>(columnInclusionRule,
                                                                                       true);
    if (columnFilter.isExcludeAll())
    {
      LOGGER
        .log(Level.INFO,
             "Not retrieving function columns, since this was not requested");
      return;
    }

    int ordinalNumber = 0;
    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getFunctionColumns(unquotedName(function.getSchema().getCatalogName()),
                          unquotedName(function.getSchema().getName()),
                          unquotedName(function.getName()),
                          null));)
    {
      while (results.next())
      {
        final String columnCatalogName = quotedName(results
          .getString("FUNCTION_CAT"));
        final String schemaName = quotedName(results
          .getString("FUNCTION_SCHEM"));
        final String functionName = quotedName(results
          .getString("FUNCTION_NAME"));
        final String columnName = quotedName(results.getString("COLUMN_NAME"));
        final String specificName = quotedName(results
          .getString("SPECIFIC_NAME"));

        final MutableFunctionColumn column = new MutableFunctionColumn(function,
                                                                       columnName);
        if (columnFilter.test(column) && function.getName().equals(functionName)
            && belongsToSchema(function, columnCatalogName, schemaName))
        {
          if (!isBlank(specificName)
              && !specificName.equals(function.getSpecificName()))
          {
            continue;
          }

          LOGGER.log(Level.FINE,
                     new StringFormat("Retrieving function column: %s.%s",
                                      function.getFullName(),
                                      columnName));

          final FunctionColumnType columnType = results
            .getEnumFromShortId("COLUMN_TYPE", FunctionColumnType.unknown);
          final int dataType = results.getInt("DATA_TYPE", 0);
          final String typeName = results.getString("TYPE_NAME");
          final int length = results.getInt("LENGTH", 0);
          final int precision = results.getInt("PRECISION", 0);
          final boolean isNullable = results
            .getShort("NULLABLE",
                      (short) DatabaseMetaData.functionNullableUnknown) == (short) DatabaseMetaData.functionNullable;
          final String remarks = results.getString("REMARKS");
          column.setOrdinalPosition(ordinalNumber++);
          column.setFunctionColumnType(columnType);
          column.setColumnDataType(lookupOrCreateColumnDataType(function
            .getSchema(), dataType, typeName));
          column.setSize(length);
          column.setPrecision(precision);
          column.setNullable(isNullable);
          column.setRemarks(remarks);

          column.addAttributes(results.getAttributes());

          function.addColumn(column);
        }
      }
    }
    catch (final AbstractMethodError | SQLFeatureNotSupportedException e)
    {
      logSQLFeatureNotSupported("JDBC driver does not support retrieving function columns",
                                e);
    }
    catch (final SQLException e)
    {
      // HYC00 = Optional feature not implemented
      if ("HYC00".equalsIgnoreCase(e.getSQLState()))
      {
        logSQLFeatureNotSupported("JDBC driver does not support retrieving function columns",
                                  e);
      }
      else
      {
        throw new SchemaCrawlerSQLException("Could not retrieve columns for function "
                                            + function, e);
      }
    }

  }

  void retrieveFunctions(final Schema schema,
                         final InclusionRule routineInclusionRule)
    throws SQLException
  {
    requireNonNull(schema, "No schema provided");

    final InclusionRuleFilter<Function> functionFilter = new InclusionRuleFilter<>(routineInclusionRule,
                                                                                   false);
    if (functionFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving functions, since this was not requested");
      return;
    }

    final Optional<Schema> schemaOptional = catalog
      .lookupSchema(schema.getFullName());
    if (!schemaOptional.isPresent())
    {
      LOGGER.log(Level.INFO,
                 new StringFormat("Cannot locate schema, so not retrieving functions for schema: %s",
                                  schema));
      return;
    }

    LOGGER.log(Level.INFO,
               new StringFormat("Retrieving functions for schema: %s", schema));

    final String catalogName = schema.getCatalogName();
    final String schemaName = schema.getName();

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getFunctions(unquotedName(catalogName), unquotedName(schemaName), "%"));)
    {
      while (results.next())
      {
        // "FUNCTION_CAT", "FUNCTION_SCHEM"
        final String functionName = quotedName(results
          .getString("FUNCTION_NAME"));
        LOGGER.log(Level.FINE,
                   new StringFormat("Retrieving function: %s.%s",
                                    schema,
                                    functionName));
        if (isBlank(functionName))
        {
          continue;
        }
        final FunctionReturnType functionType = results
          .getEnumFromShortId("FUNCTION_TYPE", FunctionReturnType.unknown);
        final String remarks = results.getString("REMARKS");
        final String specificName = results.getString("SPECIFIC_NAME");

        final MutableFunction function = new MutableFunction(schema,
                                                             functionName);
        if (functionFilter.test(function))
        {
          function.setReturnType(functionType);
          function.setSpecificName(specificName);
          function.setRemarks(remarks);
          function.addAttributes(results.getAttributes());

          catalog.addRoutine(function);
        }
      }
    }
    catch (final AbstractMethodError | SQLFeatureNotSupportedException e)
    {
      logSQLFeatureNotSupported("JDBC driver does not support retrieving functions",
                                e);
    }
    catch (final SQLException e)
    {
      // HYC00 = Optional feature not implemented
      if ("HYC00".equalsIgnoreCase(e.getSQLState()))
      {
        logSQLFeatureNotSupported("JDBC driver does not support retrieving functions",
                                  e);
      }
      else
      {
        throw new SchemaCrawlerSQLException("Could not retrieve functions", e);
      }
    }

  }

  void retrieveProcedureColumns(final MutableProcedure procedure,
                                final InclusionRule columnInclusionRule)
    throws SQLException
  {
    final InclusionRuleFilter<ProcedureColumn> columnFilter = new InclusionRuleFilter<>(columnInclusionRule,
                                                                                        true);
    if (columnFilter.isExcludeAll())
    {
      LOGGER
        .log(Level.INFO,
             "Not retrieving procedure columns, since this was not requested");
      return;
    }

    int ordinalNumber = 0;
    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getProcedureColumns(unquotedName(procedure.getSchema().getCatalogName()),
                           unquotedName(procedure.getSchema().getName()),
                           unquotedName(procedure.getName()),
                           null));)
    {
      while (results.next())
      {
        final String columnCatalogName = quotedName(results
          .getString("PROCEDURE_CAT"));
        final String schemaName = quotedName(results
          .getString("PROCEDURE_SCHEM"));
        final String procedureName = quotedName(results
          .getString("PROCEDURE_NAME"));
        final String columnName = quotedName(results.getString("COLUMN_NAME"));
        final String specificName = quotedName(results
          .getString("SPECIFIC_NAME"));

        final MutableProcedureColumn column = new MutableProcedureColumn(procedure,
                                                                         columnName);
        if (columnFilter.test(column)
            && procedure.getName().equals(procedureName)
            && belongsToSchema(procedure, columnCatalogName, schemaName))
        {
          if (!isBlank(specificName)
              && !specificName.equals(procedure.getSpecificName()))
          {
            continue;
          }

          LOGGER.log(Level.FINE,
                     new StringFormat("Retrieving procedure column: %s.%s",
                                      procedure.getFullName(),
                                      columnName));

          final ProcedureColumnType columnType = results
            .getEnumFromShortId("COLUMN_TYPE", ProcedureColumnType.unknown);
          final int dataType = results.getInt("DATA_TYPE", 0);
          final String typeName = results.getString("TYPE_NAME");
          final int length = results.getInt("LENGTH", 0);
          final int precision = results.getInt("PRECISION", 0);
          final boolean isNullable = results
            .getShort("NULLABLE",
                      (short) DatabaseMetaData.procedureNullableUnknown) == (short) DatabaseMetaData.procedureNullable;
          final String remarks = results.getString("REMARKS");
          column.setOrdinalPosition(ordinalNumber++);
          column.setProcedureColumnType(columnType);
          column.setColumnDataType(lookupOrCreateColumnDataType(procedure
            .getSchema(), dataType, typeName));
          column.setSize(length);
          column.setPrecision(precision);
          column.setNullable(isNullable);
          column.setRemarks(remarks);

          column.addAttributes(results.getAttributes());

          procedure.addColumn(column);
        }
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve columns for procedure "
                                          + procedure, e);
    }

  }

  void retrieveProcedures(final Schema schema,
                          final InclusionRule routineInclusionRule)
    throws SQLException
  {
    requireNonNull(schema, "No schema provided");

    final InclusionRuleFilter<Procedure> procedureFilter = new InclusionRuleFilter<>(routineInclusionRule,
                                                                                     false);
    if (procedureFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving procedures, since this was not requested");
      return;
    }

    final Optional<Schema> schemaOptional = catalog
      .lookupSchema(schema.getFullName());
    if (!schemaOptional.isPresent())
    {
      LOGGER.log(Level.INFO,
                 new StringFormat("Cannot locate schema, so not retrieving procedures for schema: %s",
                                  schema));
      return;
    }

    LOGGER
      .log(Level.INFO,
           new StringFormat("Retrieving procedures for schema: %s", schema));

    final String catalogName = schema.getCatalogName();
    final String schemaName = schema.getName();

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getProcedures(unquotedName(catalogName),
                     unquotedName(schemaName),
                     "%"));)
    {
      results.setDescription("retrieveProcedures");
      while (results.next())
      {
        // "PROCEDURE_CAT", "PROCEDURE_SCHEM"
        final String procedureName = quotedName(results
          .getString("PROCEDURE_NAME"));
        LOGGER.log(Level.FINE,
                   new StringFormat("Retrieving procedure: %s.%s",
                                    schema,
                                    procedureName));
        if (isBlank(procedureName))
        {
          continue;
        }
        final ProcedureReturnType procedureType = results
          .getEnumFromShortId("PROCEDURE_TYPE", ProcedureReturnType.unknown);
        final String remarks = results.getString("REMARKS");
        final String specificName = results.getString("SPECIFIC_NAME");

        final MutableProcedure procedure = new MutableProcedure(schema,
                                                                procedureName);
        if (procedureFilter.test(procedure))
        {
          procedure.setReturnType(procedureType);
          procedure.setSpecificName(specificName);
          procedure.setRemarks(remarks);
          procedure.addAttributes(results.getAttributes());

          catalog.addRoutine(procedure);
        }
      }
    }

  }

}
