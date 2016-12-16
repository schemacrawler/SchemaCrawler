/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
final class ImmutableJdbcDriverProperty
  extends AbstractProperty
  implements JdbcDriverProperty
{

  private static final long serialVersionUID = 8030156654422512161L;

  private final String description;
  private final boolean required;
  private final List<String> choices;

  ImmutableJdbcDriverProperty(final DriverPropertyInfo driverPropertyInfo)
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
      return getName().toLowerCase()
        .compareTo(otherProperty.getName().toLowerCase());
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
    return new ArrayList<>(choices);
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
