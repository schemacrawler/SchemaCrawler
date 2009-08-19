/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ProcedureColumnType;
import schemacrawler.schema.ProcedureType;
import schemacrawler.schemacrawler.InclusionRule;

/**
 * A retriever uses database metadata to get the details about the
 * database procedures.
 * 
 * @author Sualeh Fatehi
 */
final class ProcedureRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(ProcedureRetriever.class.getName());

  ProcedureRetriever(final RetrieverConnection retrieverConnection,
                     final MutableDatabase database)
    throws SQLException
  {
    super(retrieverConnection, database);
  }

  /**
   * Retrieves a list of columns from the database, for the table
   * specified.
   * 
   * @param procedure
   *        Procedure for which data is required.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveProcedureColumns(final MutableProcedure procedure,
                                final InclusionRule columnInclusionRule)
    throws SQLException
  {

    final MetadataResultSet results = new MetadataResultSet(getRetrieverConnection()
      .getMetaData().getProcedureColumns(procedure.getSchema().getParent()
                                           .getName(),
                                         procedure.getSchema().getName(),
                                         procedure.getName(),
                                         null));
    int ordinalNumber = 0;
    try
    {
      while (results.next())
      {
        final String columnCatalogName = results.getString("PROCEDURE_CAT");
        final String schemaName = results.getString("PROCEDURE_SCHEM");
        final String procedureName = results.getString("PROCEDURE_NAME");
        final String columnName = results.getString(COLUMN_NAME);

        final MutableProcedureColumn column = new MutableProcedureColumn(procedure,
                                                                         columnName);
        final String columnFullName = column.getFullName();
        if (columnInclusionRule.include(columnFullName)
            && procedure.getName().equals(procedureName)
            && belongsToSchema(procedure, columnCatalogName, schemaName))
        {
          LOGGER.log(Level.FINER, "Retrieving procedure column: " + columnName);
          final short columnType = results.getShort("COLUMN_TYPE", (short) 0);
          final int dataType = results.getInt(DATA_TYPE, 0);
          final String typeName = results.getString(TYPE_NAME);
          final int length = results.getInt("LENGTH", 0);
          final int precision = results.getInt("PRECISION", 0);
          final boolean isNullable = results
            .getShort(NULLABLE,
                      (short) DatabaseMetaData.procedureNullableUnknown) == DatabaseMetaData.procedureNullable;
          final String remarks = results.getString(REMARKS);
          column.setOrdinalPosition(ordinalNumber++);
          column
            .setProcedureColumnType(ProcedureColumnType.valueOf(columnType));
          column.setType(lookupOrCreateColumnDataType((MutableSchema) procedure
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
    finally
    {
      results.close();
    }

  }

  /**
   * Retrieves procedure metadata according to the parameters specified.
   * No column metadata is retrieved, for reasons of efficiency.
   * 
   * @param pattern
   *        Procedure name pattern for table
   * @param useRegExpPattern
   *        True is the procedure name pattern is a regular expression;
   *        false if the procedure name pattern is the JDBC pattern
   * @return A list of tables in the database that match the pattern
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveProcedures(final String catalogName,
                          final InclusionRule procedureInclusionRule)
    throws SQLException
  {
    final MetadataResultSet results = new MetadataResultSet(getRetrieverConnection()
      .getMetaData().getProcedures(catalogName,
                                   getRetrieverConnection().getSchemaPattern(),
                                   "%"));
    try
    {
      while (results.next())
      {
        // final String catalogName =
        // results.getString("PROCEDURE_CAT");
        final String schemaName = results.getString("PROCEDURE_SCHEM");
        final String procedureName = results.getString("PROCEDURE_NAME");
        LOGGER.log(Level.FINER, "Retrieving procedure: " + procedureName);
        final short procedureType = results
          .getShort("PROCEDURE_TYPE", (short) ProcedureType.unknown.getId());
        final String remarks = results.getString(REMARKS);

        final MutableSchema schema = lookupSchema(catalogName, schemaName);
        if (schema == null)
        {
          LOGGER.log(Level.FINE, String.format("Cannot find schema, %s.%s",
                                               catalogName,
                                               schemaName));
          continue;
        }

        final MutableProcedure procedure = new MutableProcedure(schema,
                                                                procedureName);
        if (procedureInclusionRule.include(procedure.getFullName()))
        {
          procedure.setType(ProcedureType.valueOf(procedureType));
          procedure.setRemarks(remarks);
          procedure.addAttributes(results.getAttributes());

          schema.addProcedure(procedure);
        }
      }
    }
    finally
    {
      results.close();
    }
  }

}
