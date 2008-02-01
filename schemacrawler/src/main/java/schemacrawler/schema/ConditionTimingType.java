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

package schemacrawler.schema;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Constraint type.
 */
public enum ConditionTimingType
{

  /** Unknown */
  unknown("unknown"),
  /** Before */
  before("BEFORE"),
  /** Instead of */
  instead_of("INSTEAD OF"),
  /** After */
  after("AFTER");

  private static final Logger LOGGER = Logger
    .getLogger(ConditionTimingType.class.getName());

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param value
   *        Sort sequence code.
   * @return Enumeration value
   */
  public static ConditionTimingType valueOfFromValue(final String value)
  {
    for (final ConditionTimingType type: ConditionTimingType.values())
    {
      if (type.getValue().equalsIgnoreCase(value))
      {
        return type;
      }
    }
    LOGGER.log(Level.FINE, "Unknown value  " + value);
    return unknown;
  }

  private final String value;

  private ConditionTimingType(final String value)
  {
    this.value = value;
  }

  /**
   * Gets the value.
   * 
   * @return Value
   */
  public final String getValue()
  {
    return value;
  }

}
