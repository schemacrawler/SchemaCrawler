/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.sql.SQLException;

import schemacrawler.schema.DatabaseObject;
import sf.util.Utilities;

/**
 * SchemaRetriever uses database metadata to get the details about the
 * schema.
 * 
 * @author sfatehi
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

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param connection
   *        An open database connection.
   * @param driverClassName
   *        Class name of the JDBC driver
   * @param schemaPatternString
   *        JDBC schema pattern, or null
   * @throws SQLException
   *         On a SQL exception
   */
  AbstractRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    this.retrieverConnection = retrieverConnection;
  }

  protected RetrieverConnection getRetrieverConnection()
  {
    return retrieverConnection;
  }

  protected boolean belongsToSchema(final DatabaseObject dbObject,
      final String catalog, final String schema)
  {
    if (dbObject == null)
    {
      return false;
    }

    boolean belongsToCatalog = true;
    boolean belongsToSchema = true;
    final String dbObjectCatalog = dbObject.getCatalogName();
    if (!Utilities.isBlank(catalog) && !Utilities.isBlank(dbObjectCatalog))
    {
      if (!catalog.equals(dbObjectCatalog))
      {
        belongsToCatalog = false;
      }
    }
    final String dbObjectSchema = dbObject.getSchemaName();
    if (!Utilities.isBlank(schema) && !Utilities.isBlank(dbObjectSchema))
    {
      if (!schema.equals(dbObjectSchema))
      {
        belongsToSchema = false;
      }
    }
    return belongsToCatalog && belongsToSchema;
  }

}
