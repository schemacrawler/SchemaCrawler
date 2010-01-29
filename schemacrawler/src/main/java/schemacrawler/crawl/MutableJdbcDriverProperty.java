/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import java.sql.DriverPropertyInfo;
import java.util.Arrays;

import schemacrawler.schema.JdbcDriverProperty;

/**
 * Represents a JDBC driver property, and it's value. Created from
 * metadata returned by a JDBC call, and other sources of information.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableJdbcDriverProperty
  extends MutableDatabaseProperty
  implements JdbcDriverProperty
{

  private static final long serialVersionUID = 8030156654422512161L;

  private final String description;
  private final boolean required;
  private final String[] choices;

  MutableJdbcDriverProperty(final DriverPropertyInfo driverPropertyInfo)
  {
    super(driverPropertyInfo.name, driverPropertyInfo.value);
    description = driverPropertyInfo.description;
    required = driverPropertyInfo.required;
    choices = driverPropertyInfo.choices;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!super.equals(obj))
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final MutableJdbcDriverProperty other = (MutableJdbcDriverProperty) obj;
    if (!Arrays.equals(choices, other.choices))
    {
      return false;
    }
    if (description == null)
    {
      if (other.description != null)
      {
        return false;
      }
    }
    else if (!description.equals(other.description))
    {
      return false;
    }
    if (required != other.required)
    {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverProperty#getChoices()
   */
  public String[] getChoices()
  {
    if (choices != null)
    {
      return choices;
    }
    else
    {
      return new String[0];
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverProperty#getDescription()
   */
  @Override
  public String getDescription()
  {
    if (description != null)
    {
      return description;
    }
    else
    {
      return "";
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverProperty#getValue()
   */
  @Override
  public String getValue()
  {
    final Object valueObject = super.getValue();
    final String value;
    if (valueObject == null)
    {
      value = null;
    }
    else if (getName().equalsIgnoreCase("password"))
    {
      value = "*****";
    }
    else
    {
      value = String.valueOf(valueObject);
    }
    return value;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Arrays.hashCode(choices);
    result = prime * result + (description == null? 0: description.hashCode());
    result = prime * result + (required? 1231: 1237);
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverProperty#isRequired()
   */
  public boolean isRequired()
  {
    return required;
  }

}
