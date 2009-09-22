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


import java.io.Serializable;

/**
 * A wrapper around java.sql.Types.
 * 
 * @author Sualeh Fatehi
 */
public final class JavaSqlType
  implements Serializable, Comparable<JavaSqlType>
{

  private static final long serialVersionUID = 2614819974745473431L;

  private final int javaSqlType;
  private final String javaSqlTypeName;
  private final String javaSqlTypeMappedClassName;
  private final Class<?> javaSqlTypeMappedClass;
  private final JavaSqlTypeGroup javaSqlTypeGroup;

  /** Unknown SQL data type. */
  public static final JavaSqlType UNKNOWN = new JavaSqlType(Integer.MAX_VALUE,
                                                            "<UNKNOWN>",
                                                            void.class,
                                                            JavaSqlTypeGroup.unknown);

  JavaSqlType(final int javaSqlType,
              final String javaSqlTypeName,
              final Class<?> javaSqlTypeMappedClass,
              final JavaSqlTypeGroup javaSqlTypeGroup)
  {
    this.javaSqlType = javaSqlType;
    this.javaSqlTypeName = javaSqlTypeName;
    this.javaSqlTypeGroup = javaSqlTypeGroup;
    //
    this.javaSqlTypeMappedClass = javaSqlTypeMappedClass;
    if (javaSqlTypeMappedClass != null)
    {
      javaSqlTypeMappedClassName = javaSqlTypeMappedClass.getCanonicalName();
    }
    else
    {
      javaSqlTypeMappedClassName = null;
    }

  }

  JavaSqlType(final int javaSqlType,
              final String javaSqlTypeName,
              final String javaSqlTypeMappedClassName,
              final JavaSqlTypeGroup javaSqlTypeGroup)
  {
    this.javaSqlType = javaSqlType;
    this.javaSqlTypeName = javaSqlTypeName;
    this.javaSqlTypeGroup = javaSqlTypeGroup;
    //
    javaSqlTypeMappedClass = null;
    this.javaSqlTypeMappedClassName = javaSqlTypeMappedClassName;
  }

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
    if (javaSqlTypeMappedClass == null)
    {
      if (other.javaSqlTypeMappedClass != null)
      {
        return false;
      }
    }
    else if (!javaSqlTypeMappedClass.getCanonicalName()
      .equals(other.javaSqlTypeMappedClass.getCanonicalName()))
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

  public Class<?> getJavaSqlTypeMappedClass()
  {
    return javaSqlTypeMappedClass;
  }

  public String getJavaSqlTypeMappedClassName()
  {
    return javaSqlTypeMappedClassName;
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
    result = prime
             * result
             + (javaSqlTypeMappedClass == null? 0: javaSqlTypeMappedClass
               .hashCode());
    result = prime * result
             + (javaSqlTypeName == null? 0: javaSqlTypeName.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return String.format("%s\t%d\t%s\t%s",
                         javaSqlTypeName,
                         javaSqlType,
                         javaSqlTypeMappedClass != null? javaSqlTypeMappedClass
                           .getCanonicalName(): javaSqlTypeMappedClassName,
                         javaSqlTypeGroup);
  }

}
