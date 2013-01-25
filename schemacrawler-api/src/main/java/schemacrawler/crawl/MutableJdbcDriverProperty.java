/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import schemacrawler.schema.JdbcDriverProperty;
import schemacrawler.schema.Property;

/**
 * Represents a JDBC driver property, and it's value. Created from
 * metadata returned by a JDBC call, and other sources of information.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableJdbcDriverProperty
  extends MutableProperty
  implements JdbcDriverProperty
{

  private static final long serialVersionUID = 8030156654422512161L;

  private final String description;
  private final boolean required;
  private final List<String> choices;

  MutableJdbcDriverProperty(final DriverPropertyInfo driverPropertyInfo)
  {
    super(driverPropertyInfo.name, driverPropertyInfo.value);
    description = driverPropertyInfo.description;
    required = driverPropertyInfo.required;

    if (driverPropertyInfo.choices == null)
    {
      choices = Collections.emptyList();
    }
    else
    {
      choices = Arrays.asList(driverPropertyInfo.choices);
      Collections.sort(choices);
    }
  }

  @Override
  public int compareTo(final Property otherProperty)
  {
    if (otherProperty == null)
    {
      return -1;
    }
    else
    {
      return getName().toLowerCase().compareTo(otherProperty.getName()
        .toLowerCase());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverProperty#getChoices()
   */
  @Override
  public Collection<String> getChoices()
  {
    return new ArrayList<String>(choices);
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
   */
  @Override
  public String getValue()
  {
    return (String) super.getValue();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverProperty#isRequired()
   */
  @Override
  public boolean isRequired()
  {
    return required;
  }

}
