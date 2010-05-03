/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import schemacrawler.schema.DatabaseObject;
import sf.util.Utility;

/**
 * Base class for retriever that uses database metadata to get the
 * details about the schema.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractRetriever
{

  static final String UNKNOWN = "<unknown>";

  /**
   * Checks whether the provided database object belongs to the
   * specified schema.
   * 
   * @param dbObject
   *        Database object to check
   * @param catalogName
   *        Database catalog to check against
   * @param schemaName
   *        Database schema to check against
   * @return Whether the database object belongs to the specified schema
   */
  static boolean belongsToSchema(final DatabaseObject dbObject,
                                 final String catalogName,
                                 final String schemaName)
  {
    if (dbObject == null)
    {
      return false;
    }

    boolean belongsToCatalog = true;
    boolean belongsToSchema = true;
    final String dbObjectCatalogName = dbObject.getSchema().getCatalogName();
    if (!Utility.isBlank(catalogName) && !Utility.isBlank(dbObjectCatalogName)
        && !catalogName.equals(dbObjectCatalogName))
    {
      belongsToCatalog = false;
    }
    final String dbObjectSchemaName = dbObject.getSchema().getSchemaName();
    if (!Utility.isBlank(schemaName) && !Utility.isBlank(dbObjectSchemaName)
        && !schemaName.equals(dbObjectSchemaName))
    {
      belongsToSchema = false;
    }
    return belongsToCatalog && belongsToSchema;
  }

  /**
   * Reads a single column result set as a list.
   * 
   * @param results
   *        Result set
   * @return List
   * @throws SQLException
   *         On an exception
   */
  static List<String> readResultsVector(final ResultSet results)
    throws SQLException
  {
    final List<String> values = new ArrayList<String>();
    try
    {
      while (results.next())
      {
        final String value = results.getString(1);
        values.add(value);
      }
    }
    finally
    {
      results.close();
    }
    return values;
  }

  private final RetrieverConnection retrieverConnection;
  final MutableDatabase database;

  AbstractRetriever()
  {
    this(null, null);
  }

  AbstractRetriever(final RetrieverConnection retrieverConnection,
                    final MutableDatabase database)
  {
    this.retrieverConnection = retrieverConnection;
    this.database = database;
  }

  protected DatabaseSystemParameters getDatabaseSystemParameters()
  {
    if (retrieverConnection != null)
    {
      return retrieverConnection.getDatabaseSystemParameters();
    }
    else
    {
      return null;
    }
  }

  Connection getDatabaseConnection()
  {
    return retrieverConnection.getConnection();
  }

  DatabaseMetaData getMetaData()
  {
    return retrieverConnection.getMetaData();
  }

  RetrieverConnection getRetrieverConnection()
  {
    return retrieverConnection;
  }

  Collection<SchemaReference> getSchemaNames()
  {
    return database.getSchemaNames();
  }

  MutableColumnDataType lookupColumnDataTypeByType(final MutableSchema schema,
                                                   final int type)
  {
    MutableColumnDataType columnDataType = schema
      .lookupColumnDataTypeByType(type);
    if (columnDataType == null)
    {
      columnDataType = database.getSystemColumnDataTypesList()
        .lookupColumnDataTypeByType(type);
    }
    return columnDataType;
  }

  /**
   * Creates a data type from the JDBC data type id, and the database
   * specific type name, if it does not exist.
   * 
   * @param schema
   *        Schema
   * @param javaSqlType
   *        JDBC data type
   * @param databaseSpecificTypeName
   *        Database specific type name
   * @return Column data type
   */
  MutableColumnDataType lookupOrCreateColumnDataType(final MutableSchema schema,
                                                     final int javaSqlType,
                                                     final String databaseSpecificTypeName)
  {
    MutableColumnDataType columnDataType = schema
      .getColumnDataType(databaseSpecificTypeName);
    if (columnDataType == null)
    {
      columnDataType = database
        .getSystemColumnDataType(databaseSpecificTypeName);
    }
    // Create new data type, if needed
    if (columnDataType == null)
    {
      columnDataType = new MutableColumnDataType(schema,
                                                 databaseSpecificTypeName);
      // Set the Java SQL type code, but no mapped Java class is
      // available, so use the defaults
      columnDataType.setType(javaSqlType, null);
      schema.addColumnDataType(columnDataType);
    }
    return columnDataType;
  }

  MutableProcedure lookupProcedure(final String catalogName,
                                   final String schemaName,
                                   final String procedureName)
  {
    MutableProcedure procedure = null;
    final MutableSchema schema = lookupSchema(catalogName, schemaName);
    if (schema != null)
    {
      procedure = schema.getProcedure(procedureName);
    }
    return procedure;
  }

  MutableSchema lookupSchema(final String catalogName, final String schemaName)
  {
    final SchemaReference schemaRef = new SchemaReference(catalogName,
                                                          schemaName);
    return database.getSchema(schemaRef);
  }

  MutableTable lookupTable(final String catalogName,
                           final String schemaName,
                           final String tableName)
  {
    MutableTable table = null;
    final MutableSchema schema = lookupSchema(catalogName, schemaName);
    if (schema != null)
    {
      table = schema.getTable(tableName);
    }
    return table;
  }

}
