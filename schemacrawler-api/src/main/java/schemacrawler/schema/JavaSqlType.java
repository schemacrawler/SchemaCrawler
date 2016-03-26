/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.schema;


import java.io.Serializable;

/**
 * A wrapper around java.sql.Types.
 *
 * @author Sualeh Fatehi
 */
public final class JavaSqlType
  implements Serializable, Comparable<JavaSqlType>
{

  public enum JavaSqlTypeGroup
  {
   unknown,
   binary,
   bit,
   character,
   id,
   integer,
   real,
   reference,
   temporal,
   url,
   xml,
   large_object,
   object;
  }

  private static final long serialVersionUID = 2614819974745473431L;

  /**
   * Unknown SQL data type.
   */
  public static final JavaSqlType UNKNOWN = new JavaSqlType(Integer.MAX_VALUE,
                                                            "<UNKNOWN>",
                                                            JavaSqlTypeGroup.unknown);
  private final int javaSqlType;
  private final String javaSqlTypeName;

  private final JavaSqlTypeGroup javaSqlTypeGroup;

  public JavaSqlType(final int javaSqlType,
                     final String javaSqlTypeName,
                     final JavaSqlTypeGroup javaSqlTypeGroup)
  {
    this.javaSqlType = javaSqlType;
    this.javaSqlTypeName = javaSqlTypeName;
    this.javaSqlTypeGroup = javaSqlTypeGroup;
  }

  @Override
  public int compareTo(final JavaSqlType otherSqlDataType)
  {
    return javaSqlTypeName.compareTo(otherSqlDataType.javaSqlTypeName);
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
    final JavaSqlType other = (JavaSqlType) obj;
    if (javaSqlType != other.javaSqlType)
    {
      return false;
    }
    if (javaSqlTypeGroup == null)
    {
      if (other.javaSqlTypeGroup != null)
      {
        return false;
      }
    }
    else if (!javaSqlTypeGroup.equals(other.javaSqlTypeGroup))
    {
      return false;
    }
    if (javaSqlTypeName == null)
    {
      if (other.javaSqlTypeName != null)
      {
        return false;
      }
    }
    else if (!javaSqlTypeName.equals(other.javaSqlTypeName))
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
  public int getJavaSqlType()
  {
    return javaSqlType;
  }

  public JavaSqlTypeGroup getJavaSqlTypeGroup()
  {
    return javaSqlTypeGroup;
  }

  /**
   * The java.sql.Types type name.
   *
   * @return java.sql.Types type names
   */
  public String getJavaSqlTypeName()
  {
    return javaSqlTypeName;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + javaSqlType;
    result = prime * result
             + (javaSqlTypeGroup == null? 0: javaSqlTypeGroup.hashCode());
    result = prime * result
             + (javaSqlTypeName == null? 0: javaSqlTypeName.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return String.format("%s\t%d\t%s",
                         javaSqlTypeName,
                         javaSqlType,
                         javaSqlTypeGroup);
  }

}
