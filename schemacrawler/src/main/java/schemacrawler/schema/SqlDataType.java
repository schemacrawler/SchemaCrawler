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

package schemacrawler.schema;


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A wrapper around java.sql.Types.
 * 
 * @author Sualeh Fatehi
 */
public final class SqlDataType
  implements Serializable
{

  private static final Logger LOGGER = Logger.getLogger(SqlDataType.class
    .getName());

  /** Unknown SQL data type. */
  public static final SqlDataType UNKNOWN = new SqlDataType(Integer.MAX_VALUE,
                                                            "<UNKNOWN>");

  private static final long serialVersionUID = 2614819974745473431L;

  private static final Map<Integer, SqlDataType> JAVA_SQL_TYPES = getJavaSqlTypes();

  /**
   * The java.sql.Types type .
   * 
   * @param type
   *        java.sql.Types type
   * @return java.sql.Types type
   */
  public static SqlDataType lookupSqlDataType(final int type)
  {
    SqlDataType sqlDataType = JAVA_SQL_TYPES.get(Integer.valueOf(type));
    if (sqlDataType == null)
    {
      sqlDataType = UNKNOWN;
    }
    return sqlDataType;
  }

  private static Map<Integer, SqlDataType> getJavaSqlTypes()
  {

    final Map<Integer, SqlDataType> javaSqlTypes = new HashMap<Integer, SqlDataType>();

    final InputStream javaSqlTypesStream = SqlDataType.class
      .getResourceAsStream("/java.sql.Types.properties");
    if (javaSqlTypesStream != null)
    {
      final Properties javaSqlTypesProperties = new Properties();
      try
      {
        javaSqlTypesProperties.load(javaSqlTypesStream);
        javaSqlTypesStream.close();
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Could not read internal resource", e);
      }
      for (final Entry<Object, Object> entry: javaSqlTypesProperties.entrySet())
      {
        if (entry.getKey() != null && entry.getValue() != null)
        {
          final Integer type = Integer.parseInt(entry.getKey().toString());
          javaSqlTypes.put(type, new SqlDataType(type, entry.getValue()
            .toString()));
        }
      }
    }

    if (javaSqlTypes.size() == 0)
    {
      final Field[] staticFields = Types.class.getFields();
      for (final Field field: staticFields)
      {
        try
        {
          final String fieldName = field.getName();
          final Integer fieldValue = (Integer) field.get(null);
          javaSqlTypes.put(fieldValue, new SqlDataType(fieldValue, fieldName));
        }
        catch (final SecurityException e)
        {
          LOGGER.log(Level.WARNING, "Could not access java.sql.Types", e);
          continue;
        }
        catch (final IllegalAccessException e)
        {
          LOGGER.log(Level.WARNING, "Could not access java.sql.Types", e);
          continue;
        }
      }
    }

    return Collections.unmodifiableMap(javaSqlTypes);
  }

  private final int type;
  private final String typeName;

  private SqlDataType(final int type, final String typeName)
  {
    this.type = type;
    this.typeName = typeName;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final SqlDataType other = (SqlDataType) obj;
    if (type != other.type)
    {
      return false;
    }
    if (typeName == null)
    {
      if (other.typeName != null)
      {
        return false;
      }
    }
    else if (!typeName.equals(other.typeName))
    {
      return false;
    }
    return true;
  }

  /**
   * The java.sql.Types type.
   * 
   * @return java.sql.Types type
   */
  public int getType()
  {
    return type;
  }

  /**
   * The java.sql.Types type name.
   * 
   * @return java.sql.Types type names
   */
  public String getTypeName()
  {
    return typeName;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + type;
    result = prime * result + (typeName == null? 0: typeName.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return String.format("%s[%d]", typeName, type);
  }

}
