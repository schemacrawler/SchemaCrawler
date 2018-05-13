/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.utility;


import static sf.util.Utility.isBlank;

import java.sql.JDBCType;
import java.sql.SQLType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.JavaSqlType.JavaSqlTypeGroup;

/**
 * Utility to work with java.sql.Types.
 *
 * @author Sualeh Fatehi
 */
public final class JavaSqlTypes
  implements Iterable<JavaSqlType>
{

  private static JavaSqlTypeGroup groupJavaSqlType(final int type)
  {

    final JavaSqlTypeGroup typeGroup;
    switch (type)
    {
      case java.sql.Types.ARRAY:
      case java.sql.Types.DISTINCT:
      case java.sql.Types.JAVA_OBJECT:
      case java.sql.Types.OTHER:
      case java.sql.Types.STRUCT:
        typeGroup = JavaSqlTypeGroup.object;
        break;
      case java.sql.Types.BINARY:
      case java.sql.Types.LONGVARBINARY:
      case java.sql.Types.VARBINARY:
        typeGroup = JavaSqlTypeGroup.binary;
        break;
      case java.sql.Types.BIT:
      case java.sql.Types.BOOLEAN:
        typeGroup = JavaSqlTypeGroup.bit;
        break;
      case java.sql.Types.CHAR:
      case java.sql.Types.LONGNVARCHAR:
      case java.sql.Types.LONGVARCHAR:
      case java.sql.Types.NCHAR:
      case java.sql.Types.NVARCHAR:
      case java.sql.Types.VARCHAR:
        typeGroup = JavaSqlTypeGroup.character;
        break;
      case java.sql.Types.ROWID:
        typeGroup = JavaSqlTypeGroup.id;
        break;
      case java.sql.Types.BIGINT:
      case java.sql.Types.INTEGER:
      case java.sql.Types.SMALLINT:
      case java.sql.Types.TINYINT:
        typeGroup = JavaSqlTypeGroup.integer;
        break;
      case java.sql.Types.BLOB:
      case java.sql.Types.CLOB:
      case java.sql.Types.NCLOB:
        typeGroup = JavaSqlTypeGroup.large_object;
        break;
      case java.sql.Types.DECIMAL:
      case java.sql.Types.DOUBLE:
      case java.sql.Types.FLOAT:
      case java.sql.Types.NUMERIC:
      case java.sql.Types.REAL:
        typeGroup = JavaSqlTypeGroup.real;
        break;
      case java.sql.Types.REF:
        typeGroup = JavaSqlTypeGroup.reference;
        break;
      case java.sql.Types.DATE:
      case java.sql.Types.TIME:
      case java.sql.Types.TIMESTAMP:
        typeGroup = JavaSqlTypeGroup.temporal;
        break;
      case java.sql.Types.DATALINK:
        typeGroup = JavaSqlTypeGroup.url;
        break;
      case java.sql.Types.SQLXML:
        typeGroup = JavaSqlTypeGroup.xml;
        break;
      default:
        typeGroup = JavaSqlTypeGroup.unknown;
        break;
    }
    return typeGroup;
  }

  private static Map<Integer, JavaSqlType> mapJavaSqlTypes()
  {
    final Map<Integer, JavaSqlType> javaSqlTypes = new HashMap<>();
    for (final SQLType sqlType: JDBCType.values())
    {
      final Integer sqlTypeInt = sqlType.getVendorTypeNumber();
      final JavaSqlTypeGroup sqlTypeGroup = groupJavaSqlType(sqlTypeInt);

      final JavaSqlType javaSqlType = new JavaSqlType(sqlType, sqlTypeGroup);
      javaSqlTypes.put(sqlTypeInt, javaSqlType);
    }

    return javaSqlTypes;
  }

  private final Map<Integer, JavaSqlType> javaSqlTypeMap;

  public JavaSqlTypes()
  {
    javaSqlTypeMap = mapJavaSqlTypes();
  }

  /**
   * Lookup java.sql.Types type, and return more detailed information,
   * including the mapped Java class.
   *
   * @param typeName
   *        java.sql.Types type name
   * @return JavaSqlType type
   */
  public JavaSqlType getFromJavaSqlTypeName(final String typeName)
  {
    JavaSqlType sqlDataType = JavaSqlType.UNKNOWN;
    if (isBlank(typeName))
    {
      return sqlDataType;
    }

    for (final JavaSqlType javaSqlType: javaSqlTypeMap.values())
    {
      if (typeName.equals(javaSqlType.getJavaSqlTypeName()))
      {
        sqlDataType = javaSqlType;
        break;
      }
    }
    return sqlDataType;
  }

  @Override
  public Iterator<JavaSqlType> iterator()
  {
    return javaSqlTypeMap.values().iterator();
  }

  public int size()
  {
    return javaSqlTypeMap.size();
  }

  @Override
  public String toString()
  {
    return javaSqlTypeMap.toString();
  }

  public JavaSqlType valueOf(final int key)
  {
    if (javaSqlTypeMap.containsKey(key))
    {
      return javaSqlTypeMap.get(key);
    }
    else
    {
      return JavaSqlType.UNKNOWN;
    }
  }

}
