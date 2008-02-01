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
 * An enumeration wrapper around index sort sequences.
 */
public enum IndexSortSequence
{

  /** Unknown */
  unknown("unknown"),
  /** Ascending. */
  ascending("A"),
  /** Descending. */
  descending("D");

  private static final Logger LOGGER = Logger.getLogger(IndexSortSequence.class
    .getName());

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param code
   *        Sort sequence code.
   * @return Enumeration value
   */
  public static IndexSortSequence valueOfFromCode(final String code)
  {
    for (final IndexSortSequence type: IndexSortSequence.values())
    {
      if (type.getCode().equalsIgnoreCase(code))
      {
        return type;
      }
    }
    LOGGER.log(Level.FINE, "Unknown code  " + code);
    return unknown;
  }

  private final String code;

  private IndexSortSequence(final String code)
  {
    this.code = code;
  }

  /**
   * Index sort sequence code.
   * 
   * @return Index sort sequence code
   */
  public String getCode()
  {
    return code;
  }

}
