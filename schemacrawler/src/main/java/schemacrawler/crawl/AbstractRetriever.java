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


import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.utility.Utility;

/**
 * Base class for retriever that uses database metadata to get the
 * details about the schema.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractRetriever
{

  protected static final String COLUMN_NAME = "COLUMN_NAME";

  protected static final String DATA_TYPE = "DATA_TYPE";
  protected static final String KEY_SEQ = "KEY_SEQ";
  protected static final String NULLABLE = "NULLABLE";
  protected static final String ORDINAL_POSITION = "ORDINAL_POSITION";
  protected static final String REMARKS = "REMARKS";
  protected static final String TABLE_NAME = "TABLE_NAME";
  protected static final String TYPE_NAME = "TYPE_NAME";
  protected static final String UNKNOWN = "<unknown>";

  protected static final int FETCHSIZE = 5;

  private final RetrieverConnection retrieverConnection;
  protected final MutableDatabase database;

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
  protected boolean belongsToSchema(final DatabaseObject dbObject,
                                    final String catalogName,
                                    final String schemaName)
  {
    if (dbObject == null)
    {
      return false;
    }

    boolean belongsToCatalog = true;
    boolean belongsToSchema = true;
    final String dbObjectCatalogName = dbObject.getSchema().getCatalog()
      .getName();
    if (!Utility.isBlank(catalogName) && !Utility.isBlank(dbObjectCatalogName)
        && !catalogName.equals(dbObjectCatalogName))
    {
      belongsToCatalog = false;
    }
    final String dbObjectSchemaName = dbObject.getSchema().getName();
    if (!Utility.isBlank(schemaName) && !Utility.isBlank(dbObjectSchemaName)
        && !schemaName.equals(dbObjectSchemaName))
    {
      belongsToSchema = false;
    }
    return belongsToCatalog && belongsToSchema;
  }

  protected Connection getDatabaseConnection()
  {
    return retrieverConnection.getConnection();
  }

  protected RetrieverConnection getRetrieverConnection()
  {
    return retrieverConnection;
  }

  protected MutableProcedure lookupProcedure(final String catalogName,
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

  protected MutableSchema lookupSchema(final String catalogName,
                                       final String schemaName)
  {
    MutableSchema schema = null;
    final Catalog catalog = database.getCatalog(catalogName);
    if (catalog != null)
    {
      schema = (MutableSchema) catalog.getSchema(schemaName);
    }
    return schema;
  }

  protected MutableTable lookupTable(final String catalogName,
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
