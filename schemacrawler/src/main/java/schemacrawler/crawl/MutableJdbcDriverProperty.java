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
 * Represents a JDBC driver property, and it's value. Created from
 * metadata returned by a JDBC call, and other sources of information.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableJdbcDriverProperty
  extends AbstractNamedObject
  implements JdbcDriverProperty
{

  private static final long serialVersionUID = 8030156654422512161L;

  private final String description;
  private final boolean required;
  private final String value;
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
