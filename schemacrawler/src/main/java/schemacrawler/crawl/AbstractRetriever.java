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


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.DatabaseObject;
import sf.util.Utilities;

/**
 * SchemaRetriever uses database metadata to get the details about the
 * schema.
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
  private static final Logger LOGGER = Logger.getLogger(AbstractRetriever.class
    .getName());

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
    if (!Utilities.isBlank(catalog) && !Utilities.isBlank(dbObjectCatalog)
        && !catalog.equals(dbObjectCatalog))
    {
      belongsToCatalog = false;
    }
    final String dbObjectSchema = dbObject.getSchemaName();
    if (!Utilities.isBlank(schema) && !Utilities.isBlank(dbObjectSchema)
        && !schema.equals(dbObjectSchema))
    {
      belongsToSchema = false;
    }
    return belongsToCatalog && belongsToSchema;
  }

  protected RetrieverConnection getRetrieverConnection()
  {
    return retrieverConnection;
  }

  /**
   * Reads the value of a column from the result set as an integer. If
   * the value was null, returns the default.
   * 
   * @param results
   *        Result set
   * @param columnName
   *        Column name
   * @return Integer value of the column, or the default if not
   *         available
   */
  protected int readInt(final ResultSet results,
                        final String columnName,
                        final int defaultValue)
  {
    int value = defaultValue;
    if (results != null && !Utilities.isBlank(columnName))
    {
      try
      {
        value = results.getInt(columnName);
        if (results.wasNull())
        {
          value = defaultValue;
        }
        return value;
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.FINE, "Could not read integer value for column "
                               + columnName);
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a long. If the
   * value was null, returns the default.
   * 
   * @param results
   *        Result set
   * @param columnName
   *        Column name
   * @return Long value of the column, or the default if not available
   */
  protected long readLong(final ResultSet results,
                          final String columnName,
                          final long defaultValue)
  {
    long value = defaultValue;
    if (results != null && !Utilities.isBlank(columnName))
    {
      try
      {
        value = results.getLong(columnName);
        if (results.wasNull())
        {
          value = defaultValue;
        }
        return value;
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.FINE, "Could not read long value for column "
                               + columnName);
      }
    }
    return value;
  }

  /**
   * Reads the value of a column from the result set as a short. If the
   * value was null, returns the default.
   * 
   * @param results
   *        Result set
   * @param columnName
   *        Column name
   * @return Short value of the column, or the default if not available
   */
  protected short readShort(final ResultSet results,
                            final String columnName,
                            final short defaultValue)
  {
    short value = defaultValue;
    if (results != null && !Utilities.isBlank(columnName))
    {
      try
      {
        value = results.getShort(columnName);
        if (results.wasNull())
        {
          value = defaultValue;
        }
        return value;
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.FINE, "Could not read short value for column "
                               + columnName);
      }
    }
    return value;
  }

}
