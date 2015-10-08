/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import schemacrawler.schema.SchemaReference;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import sf.util.Utility;

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
                   final MutableCatalog catalog)
                     throws SQLException
  {
    super(retrieverConnection, catalog);
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
          if (!Utility.isBlank(specificName)
              && !specificName.equals(function.getSpecificName()))
          {
            continue;
          }

          LOGGER.log(Level.FINER, "Retrieving function column: " + columnName);
          final short columnType = results.getShort("COLUMN_TYPE", (short) 0);
          final int dataType = results.getInt("DATA_TYPE", 0);
          final String typeName = results.getString("TYPE_NAME");
          final int length = results.getInt("LENGTH", 0);
          final int precision = results.getInt("PRECISION", 0);
          final boolean isNullable = results
            .getShort("NULLABLE",
                      (short) DatabaseMetaData.functionNullableUnknown) == (short) DatabaseMetaData.functionNullable;
          final String remarks = results.getString("REMARKS");
          column.setOrdinalPosition(ordinalNumber++);
          column.setFunctionColumnType(FunctionColumnType.valueOf(columnType));
          column.setColumnDataType(lookupOrCreateColumnDataType(
                                                                function
                                                                  .getSchema(),
                                                                dataType,
                                                                typeName));
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
      LOGGER.log(Level.WARNING,
                 "JDBC driver does not support retrieving functions",
                 e);
    }
    catch (final SQLException e)
    {
      // HYC00 = Optional feature not implemented
      if ("HYC00".equalsIgnoreCase(e.getSQLState()))
      {
        LOGGER.log(Level.WARNING,
                   "JDBC driver does not support retrieving function columns",
                   e);
      }
      else
      {
        throw new SchemaCrawlerSQLException("Could not retrieve columns for function "
                                            + function, e);
      }
    }

  }

  void retrieveFunctions(final String catalogName,
                         final String schemaName,
                         final InclusionRule routineInclusionRule)
                           throws SQLException
  {
    final InclusionRuleFilter<Function> functionFilter = new InclusionRuleFilter<>(routineInclusionRule,
                                                                                   false);
    if (functionFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving functions, since this was not requested");
      return;
    }

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getFunctions(unquotedName(catalogName), unquotedName(schemaName), "%"));)
    {
      while (results.next())
      {
        // "FUNCTION_CAT", "FUNCTION_SCHEM"
        final String functionName = quotedName(results
          .getString("FUNCTION_NAME"));
        LOGGER.log(Level.FINER, "Retrieving function: " + functionName);
        final short functionType = results
          .getShort("FUNCTION_TYPE",
                    (short) FunctionReturnType.unknown.getId());
        final String remarks = results.getString("REMARKS");
        final String specificName = results.getString("SPECIFIC_NAME");

        final Schema schema = new SchemaReference(catalogName, schemaName);
        final MutableFunction function = new MutableFunction(schema,
                                                             functionName);
        if (functionFilter.test(function))
        {
          function.setReturnType(FunctionReturnType.valueOf(functionType));
          function.setSpecificName(specificName);
          function.setRemarks(remarks);
          function.addAttributes(results.getAttributes());

          catalog.addRoutine(function);
        }
      }
    }
    catch (final AbstractMethodError | SQLFeatureNotSupportedException e)
    {
      LOGGER.log(Level.WARNING,
                 "JDBC driver does not support retrieving functions",
                 e);
    }
    catch (final SQLException e)
    {
      // HYC00 = Optional feature not implemented
      if ("HYC00".equalsIgnoreCase(e.getSQLState()))
      {
        LOGGER.log(Level.WARNING,
                   "JDBC driver does not support retrieving functions",
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
          if (!Utility.isBlank(specificName)
              && !specificName.equals(procedure.getSpecificName()))
          {
            continue;
          }

          LOGGER.log(Level.FINER, "Retrieving procedure column: " + columnName);
          final short columnType = results.getShort("COLUMN_TYPE", (short) 0);
          final int dataType = results.getInt("DATA_TYPE", 0);
          final String typeName = results.getString("TYPE_NAME");
          final int length = results.getInt("LENGTH", 0);
          final int precision = results.getInt("PRECISION", 0);
          final boolean isNullable = results
            .getShort("NULLABLE",
                      (short) DatabaseMetaData.procedureNullableUnknown) == (short) DatabaseMetaData.procedureNullable;
          final String remarks = results.getString("REMARKS");
          column.setOrdinalPosition(ordinalNumber++);
          column
            .setProcedureColumnType(ProcedureColumnType.valueOf(columnType));
          column.setColumnDataType(lookupOrCreateColumnDataType(
                                                                procedure
                                                                  .getSchema(),
                                                                dataType,
                                                                typeName));
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

  void retrieveProcedures(final String catalogName,
                          final String schemaName,
                          final InclusionRule routineInclusionRule)
                            throws SQLException
  {
    final InclusionRuleFilter<Procedure> procedureFilter = new InclusionRuleFilter<>(routineInclusionRule,
                                                                                     false);
    if (procedureFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving procedures, since this was not requested");
      return;
    }

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getProcedures(unquotedName(catalogName),
                     unquotedName(schemaName),
                     "%"));)
    {
      while (results.next())
      {
        // "PROCEDURE_CAT", "PROCEDURE_SCHEM"
        final String procedureName = quotedName(results
          .getString("PROCEDURE_NAME"));
        LOGGER.log(Level.FINER, "Retrieving procedure: " + procedureName);
        final short procedureType = results
          .getShort("PROCEDURE_TYPE",
                    (short) ProcedureReturnType.unknown.getId());
        final String remarks = results.getString("REMARKS");
        final String specificName = results.getString("SPECIFIC_NAME");

        final Schema schema = new SchemaReference(catalogName, schemaName);
        final MutableProcedure procedure = new MutableProcedure(schema,
                                                                procedureName);
        if (procedureFilter.test(procedure))
        {
          procedure.setReturnType(ProcedureReturnType.valueOf(procedureType));
          procedure.setSpecificName(specificName);
          procedure.setRemarks(remarks);
          procedure.addAttributes(results.getAttributes());

          catalog.addRoutine(procedure);
        }
      }
    }

  }

}
