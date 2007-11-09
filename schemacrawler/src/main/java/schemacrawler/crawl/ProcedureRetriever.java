/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.NamedObjectList.NamedObjectSort;
import schemacrawler.schema.ProcedureColumnType;
import schemacrawler.schema.ProcedureType;

/**
 * SchemaRetriever uses database metadata to get the details about the
 * schema.
 * 
 * @author Sualeh Fatehi
 */
final class ProcedureRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(ProcedureRetriever.class.getName());

  ProcedureRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    super(retrieverConnection);
  }

  /**
   * Retrieves a list of columns from the database, for the table
   * specified.
   * 
   * @param procedure
   *        Table for which data is required.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveProcedureColumns(final MutableProcedure procedure,
                                final InclusionRule columnInclusionRule,
                                final NamedObjectList<MutableColumnDataType> columnDataTypes)
    throws SQLException
  {

    final ResultSet results;

    results = getRetrieverConnection().getMetaData()
      .getProcedureColumns(getRetrieverConnection().getCatalog(),
                           procedure.getSchemaName(),
                           procedure.getName(),
                           null);
    int ordinalNumber = 0;
    while (results.next())
    {

      final String procedureName = results.getString("PROCEDURE_NAME");
      final String columnName = results.getString(COLUMN_NAME);
      LOGGER.log(Level.FINEST, "Retrieving procedure column: " + columnName);
      final short columnType = readShort(results, "COLUMN_TYPE", (short) 0);
      final int dataType = readInt(results, DATA_TYPE, 0);
      final String typeName = results.getString(TYPE_NAME);
      final int length = readInt(results, "LENGTH", 0);
      final int precision = readInt(results, "PRECISION", 0);
      final boolean isNullable = readShort(results,
                                           NULLABLE,
                                           (short) DatabaseMetaData.procedureNullableUnknown) == DatabaseMetaData.procedureNullable;
      final String remarks = results.getString(REMARKS);
      // Note: If the procedure name contains an underscore character,
      // this is a
      // wildcard character. We need to do another check to see if the
      // procedure
      // name matches.
      if (columnInclusionRule.include(columnName)
          && procedure.getName().equals(procedureName))
      {
        final MutableProcedureColumn column = new MutableProcedureColumn(columnName,
                                                                         procedure);
        column.setOrdinalPosition(ordinalNumber++);
        column.setProcedureColumnType(ProcedureColumnType.valueOf(columnType));
        lookupAndSetDataType(column, dataType, typeName, columnDataTypes);
        column.setSize(length);
        column.setPrecision(precision);
        column.setNullable(isNullable);
        column.setRemarks(remarks);

        procedure.addColumn(column);
      }
    }
    results.close();

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
  NamedObjectList<MutableProcedure> retrieveProcedures(final InclusionRule procedureInclusionRule)
    throws SQLException
  {

    final NamedObjectList<MutableProcedure> procedures = new NamedObjectList<MutableProcedure>(NamedObjectSort.alphabetical);

    final ResultSet results = getRetrieverConnection().getMetaData()
      .getProcedures(null, getRetrieverConnection().getSchemaPattern(), "%");
    try
    {
      results.setFetchSize(FETCHSIZE);
    }
    catch (final NullPointerException e)
    {
      // Need this catch for the JDBC/ ODBC driver
      LOGGER.log(Level.WARNING, "", e);
    }
    while (results.next())
    {
      final String schema = results.getString("PROCEDURE_SCHEM");
      final String catalog = results.getString("PROCEDURE_CAT");
      final String procedureName = results.getString("PROCEDURE_NAME");
      LOGGER.log(Level.FINEST, "Retrieving procedure: " + procedureName);
      final short procedureType = readShort(results,
                                            "PROCEDURE_TYPE",
                                            (short) ProcedureType.unknown
                                              .getId());
      final String remarks = results.getString(REMARKS);

      if (procedureInclusionRule.include(procedureName))
      {
        final MutableProcedure procedure = new MutableProcedure(catalog,
                                                                schema,
                                                                procedureName);
        procedure.setType(ProcedureType.valueOf(procedureType));
        procedure.setRemarks(remarks);
        // add it to the list
        procedures.add(procedure);
      }
    }
    results.close();

    return procedures;

  }

}
