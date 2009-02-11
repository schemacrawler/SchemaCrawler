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

import schemacrawler.schema.DatabaseObject;

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

  AbstractRetriever()
  {
    this(null);
  }

  AbstractRetriever(final RetrieverConnection retrieverConnection)
  {
    this.retrieverConnection = retrieverConnection;
  }

  /**
   * Checks whether the provided database object belongs to the
   * specified schema.
   * 
   * @param dbObject
   *        Database object to check
   * @param catalog
   *        Database catalog to check against
   * @param schema
   *        Database schema to check against
   * @return Whether the database object belongs to the specified schema
   */
  protected boolean belongsToSchema(final DatabaseObject dbObject,
                                    final String catalog,
                                    final String schema)
  {
    if (dbObject == null)
    {
      return false;
    }

    boolean belongsToCatalog = true;
    boolean belongsToSchema = true;
    final String dbObjectCatalog = dbObject.getCatalogName();
    if (!(catalog == null || catalog.trim().length() == 0)
        && !(dbObjectCatalog == null || dbObjectCatalog.trim().length() == 0)
        && !catalog.equals(dbObjectCatalog))
    {
      belongsToCatalog = false;
    }
    final String dbObjectSchema = dbObject.getSchemaName();
    if (!(schema == null || schema.trim().length() == 0)
        && !(dbObjectSchema == null || dbObjectSchema.trim().length() == 0)
        && !schema.equals(dbObjectSchema))
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

}
