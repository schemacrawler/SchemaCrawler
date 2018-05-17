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
package schemacrawler.schema;


import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.sql.SQLType;

/**
 * A wrapper around java.sql.Types.
 *
 * @author Sualeh Fatehi
 */
public final class JavaSqlType
  implements SQLType, Serializable, Comparable<JavaSqlType>
{

  private static final long serialVersionUID = 2614819974745473431L;

  /**
   * Unknown SQL data type.
   */
  public static final JavaSqlType UNKNOWN = new JavaSqlType(Integer.MAX_VALUE,
                                                            "<UNKNOWN>",
                                                            java.lang.Object.class,
                                                            JavaSqlTypeGroup.unknown);

  private final SQLType sqlType;
  private final Class<?> defaultMappedClass;
  private final JavaSqlTypeGroup javaSqlTypeGroup;

  public JavaSqlType(final SQLType sqlType,
                     final Class<?> defaultMappedClass,
                     final JavaSqlTypeGroup javaSqlTypeGroup)
  {
    this.sqlType = requireNonNull(sqlType, "No SQLType provided");
    this.defaultMappedClass = requireNonNull(defaultMappedClass,
                                             "Np default mapped class provided");
    this.javaSqlTypeGroup = requireNonNull(javaSqlTypeGroup,
                                           "No SQLType group provided");
  }

  private JavaSqlType(final int typeNumber,
                      final String typeName,
                      final Class<?> defaultMappedClass,
                      final JavaSqlTypeGroup sqlTypeGroup)
  {
    this(new SQLType()
    {

      @Override
      public String getName()
      {
        return typeName;
      }

      @Override
      public String getVendor()
      {
        return "SchemaCrawler";
      }

      @Override
      public Integer getVendorTypeNumber()
      {
        return typeNumber;
      }
    }, defaultMappedClass, sqlTypeGroup);
  }

  @Override
  public int compareTo(final JavaSqlType otherSqlDataType)
  {
    return sqlType.getName().compareTo(otherSqlDataType.sqlType.getName());
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
    if (sqlType == null)
    {
      if (other.sqlType != null)
      {
        return false;
      }
    }
    else if (!sqlType.getVendorTypeNumber()
      .equals(other.sqlType.getVendorTypeNumber()))
    {
      return false;
    }
    return true;
  }

  public Class<?> getDefaultMappedClass()
  {
    return defaultMappedClass;
  }

  /**
   * The java.sql.Types type.
   *
   * @return java.sql.Types type
   * @deprecated
   */
  @Deprecated
  public int getJavaSqlType()
  {
    return getVendorTypeNumber();
  }

  public JavaSqlTypeGroup getJavaSqlTypeGroup()
  {
    return javaSqlTypeGroup;
  }

  /**
   * The java.sql.Types type name.
   *
   * @return java.sql.Types type names
   * @deprecated
   */
  @Deprecated
  public String getJavaSqlTypeName()
  {
    return getName();
  }

  @Override
  public String getName()
  {
    return sqlType.getName();
  }

  @Override
  public String getVendor()
  {
    return sqlType.getVendor();
  }

  @Override
  public Integer getVendorTypeNumber()
  {
    final Integer vendorTypeNumber = sqlType.getVendorTypeNumber();
    if (vendorTypeNumber != null)
    {
      return vendorTypeNumber;
    }
    else
    {
      return Integer.valueOf(Integer.MAX_VALUE);
    }
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (sqlType == null? 0: sqlType.getVendorTypeNumber());
    return result;
  }

  @Override
  public String toString()
  {
    return String.format("%s\t%d\t%s",
                         sqlType.getName(),
                         sqlType.getVendorTypeNumber(),
                         javaSqlTypeGroup);
  }

}
