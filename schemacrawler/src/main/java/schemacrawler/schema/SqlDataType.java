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
package schemacrawler.schema;


import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper around java.sql.Types.
 * 
 * @author Sualeh Fatehi
 */
public final class SqlDataType
{

  /** Unknown SQL data type. */
  public static final SqlDataType UNKNOWN = new SqlDataType(Integer.MAX_VALUE,
                                                            "<UNKNOWN>");

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
        continue;
      }
      catch (final IllegalAccessException e)
      {
        continue;
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

}
