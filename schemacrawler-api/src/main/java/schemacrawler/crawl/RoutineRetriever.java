/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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

import schemacrawler.schema.FunctionColumnType;
import schemacrawler.schema.FunctionReturnType;
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

  private static final Logger LOGGER = Logger.getLogger(RoutineRetriever.class
    .getName());

  RoutineRetriever(final RetrieverConnection retrieverConnection,
                   final MutableDatabase database)
    throws SQLException
  {
    super(retrieverConnection, database);
  }

  void retrieveFunctionColumns(final MutableFunction function,
                               final InclusionRule columnInclusionRule)
    throws SQLException
  {
    MetadataResultSet results = null;
    int ordinalNumber = 0;
    try
    {
      results = new MetadataResultSet(getMetaData()
        .getFunctionColumns(unquotedName(function.getSchema().getCatalogName()),
                            unquotedName(function.getSchema().getName()),
                            unquotedName(function.getName()),
                            null));

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
        final String columnFullName = column.getFullName();
        if (columnInclusionRule.include(columnFullName)
            && function.getName().equals(functionName)
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
          column.setType(lookupOrCreateColumnDataType(function.getSchema(),
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
    catch (final AbstractMethodError e)
    {
      LOGGER.log(Level.WARNING,
                 "JDBC driver does not support retrieving functions",
                 e);
    }
    catch (final SQLFeatureNotSupportedException e)
    {
      LOGGER.log(Level.WARNING,
                 "JDBC driver does not support retrieving functions",
                 e);
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve columns for function "
                                              + function,
                                          e);
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }

  }

  void retrieveFunctions(final String catalogName,
                         final String schemaName,
                         final InclusionRule routineInclusionRule)
    throws SQLException
  {
    if (routineInclusionRule == null
        || routineInclusionRule.equals(InclusionRule.EXCLUDE_ALL))
    {
      return;
    }

    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(getMetaData()
        .getFunctions(unquotedName(catalogName), unquotedName(schemaName), "%"));

      while (results.next())
      {
        // "FUNCTION_CAT", "FUNCTION_SCHEM"
        final String functionName = quotedName(results
          .getString("FUNCTION_NAME"));
        LOGGER.log(Level.FINER, "Retrieving function: " + functionName);
        final short functionType = results
          .getShort("FUNCTION_TYPE", (short) FunctionReturnType.unknown.getId());
        final String remarks = results.getString("REMARKS");
        final String specificName = results.getString("SPECIFIC_NAME");

        final Schema schema = new SchemaReference(catalogName, schemaName);
        final MutableFunction function = new MutableFunction(schema,
                                                             functionName);
        if (routineInclusionRule.include(function.getFullName()))
        {
          function.setType(FunctionReturnType.valueOf(functionType));
          function.setSpecificName(specificName);
          function.setRemarks(remarks);
          function.addAttributes(results.getAttributes());

          database.addRoutine(function);
        }
      }
    }
    catch (final AbstractMethodError e)
    {
      LOGGER.log(Level.WARNING,
                 "JDBC driver does not support retrieving functions",
                 e);
    }
    catch (final SQLFeatureNotSupportedException e)
    {
      LOGGER.log(Level.WARNING,
                 "JDBC driver does not support retrieving functions",
                 e);
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }
  }

  void retrieveProcedureColumns(final MutableProcedure procedure,
                                final InclusionRule columnInclusionRule)
    throws SQLException
  {
    MetadataResultSet results = null;
    int ordinalNumber = 0;
    try
    {
      results = new MetadataResultSet(getMetaData()
        .getProcedureColumns(unquotedName(procedure.getSchema()
                               .getCatalogName()),
                             unquotedName(procedure.getSchema().getName()),
                             unquotedName(procedure.getName()),
                             null));

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
        final String columnFullName = column.getFullName();
        if (columnInclusionRule.include(columnFullName)
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
          column.setType(lookupOrCreateColumnDataType(procedure.getSchema(),
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
                                              + procedure,
                                          e);
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }

  }

  void retrieveProcedures(final String catalogName,
                          final String schemaName,
                          final InclusionRule routineInclusionRule)
    throws SQLException
  {
    if (routineInclusionRule == null
        || routineInclusionRule.equals(InclusionRule.EXCLUDE_ALL))
    {
      return;
    }

    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(getMetaData()
        .getProcedures(unquotedName(catalogName), unquotedName(schemaName), "%"));

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
        if (routineInclusionRule.include(procedure.getFullName()))
        {
          procedure.setType(ProcedureReturnType.valueOf(procedureType));
          procedure.setSpecificName(specificName);
          procedure.setRemarks(remarks);
          procedure.addAttributes(results.getAttributes());

          database.addRoutine(procedure);
        }
      }
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }
  }
}
