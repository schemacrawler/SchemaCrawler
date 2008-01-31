/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

  /**
   * A brief description of the property, which may be null.
   */
  private String description = null;

  /**
   * The <code>required</code> field is <code>true</code> if a value
   * must be supplied for this property during
   * <code>Driver.connect</code> and <code>false</code> otherwise.
   */
  private boolean required = false;

  /**
   * The <code>value</code> field specifies the current value of the
   * property, based on a combination of the information supplied to the
   * method <code>getPropertyInfo</code>, the Java environment, and
   * the driver-supplied default values. This field may be null if no
   * value is known.
   */
  private String value = null;

  /**
   * An array of possible values if the value for the field
   * <code>DriverPropertyInfo.value</code> may be selected from a
   * particular set of values; otherwise null.
   */
  private String[] choices = null;

  MutableJdbcDriverProperty(final DriverPropertyInfo driverPropertyInfo)
  {
    super(driverPropertyInfo.name);
    description = driverPropertyInfo.description;
    required = driverPropertyInfo.required;
    value = driverPropertyInfo.value;
    choices = driverPropertyInfo.choices;
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
  public String getValue()
  {
    if (getName().equalsIgnoreCase("password") && value != null)
    {
      return "*****";
    }
    return value;
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
