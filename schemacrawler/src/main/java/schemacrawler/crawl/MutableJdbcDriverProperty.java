/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
  private final String description;

  /**
   * The <code>required</code> field is <code>true</code> if a value
   * must be supplied for this property during
   * <code>Driver.connect</code> and <code>false</code> otherwise.
   */
  private final boolean required;

  /**
   * The <code>value</code> field specifies the current value of the
   * property, based on a combination of the information supplied to the
   * method <code>getPropertyInfo</code>, the Java environment, and
   * the driver-supplied default values. This field may be null if no
   * value is known.
   */
  private final String value;

  /**
   * An array of possible values if the value for the field
   * <code>DriverPropertyInfo.value</code> may be selected from a
   * particular set of values; otherwise null.
   */
  private final String[] choices;

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
