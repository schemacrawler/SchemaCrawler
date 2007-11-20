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

package schemacrawler.crawl;


import java.sql.DriverPropertyInfo;

import schemacrawler.schema.JdbcDriverProperty;

/**
 * Represents database metadata. Created from metadata returned by a
 * JDBC call, and other sources of information.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableJdbcDriverProperty
  extends AbstractNamedObject
  implements JdbcDriverProperty
{

  private static final long serialVersionUID = 8030156654422512161L;

  private final DriverPropertyInfo driverPropertyInfo;

  MutableJdbcDriverProperty(final DriverPropertyInfo driverPropertyInfo)
  {
    super(driverPropertyInfo.name);
    this.driverPropertyInfo = driverPropertyInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverProperty#getChoices()
   */
  public String[] getChoices()
  {
    if (hasChoices())
    {
      return driverPropertyInfo.choices;
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
  public String getDescription()
  {
    if (driverPropertyInfo.description != null)
    {
      return driverPropertyInfo.description;
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
  public String getValue()
  {
    String value = driverPropertyInfo.value;
    if (getName().equalsIgnoreCase("password") && value != null)
    {
      return "*****";
    }
    return value;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverProperty#hasChoices()
   */
  public boolean hasChoices()
  {
    return driverPropertyInfo.choices != null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverProperty#isRequired()
   */
  public boolean isRequired()
  {
    return driverPropertyInfo.required;
  }

}
