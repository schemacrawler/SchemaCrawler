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


import java.io.Serializable;

/**
 * A wrapper around java.sql.Types.
 * 
 * @author Sualeh Fatehi
 */
public final class SqlDataType
  implements Serializable, Comparable<SqlDataType>
{

  private static final long serialVersionUID = 2614819974745473431L;

  private final int type;
  private final String typeName;

  SqlDataType(final int type, final String typeName)
  {
    this.type = type;
    this.typeName = typeName;
  }

  public int compareTo(final SqlDataType otherSqlDataType)
  {
    return typeName.compareTo(otherSqlDataType.typeName);
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
    return String.format("%s=%d", typeName, type);
  }

}
