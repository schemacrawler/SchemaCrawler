/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.schema;


import java.io.Serializable;
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
  implements Serializable
{

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
