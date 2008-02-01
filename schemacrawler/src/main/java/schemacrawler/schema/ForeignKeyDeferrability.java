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


import java.sql.DatabaseMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The deferrability value for foreign keys.
 */
public enum ForeignKeyDeferrability
{

  /** Unknown */
  unknown(-1, "unknown"),
  /** Initially deferred. */
  initiallyDeferred(DatabaseMetaData.importedKeyInitiallyDeferred,
    "initially deferred"),
  /** Initially immediate. */
  initiallyImmediate(DatabaseMetaData.importedKeyInitiallyImmediate,
    "initially immediate"),
  /** Not deferrable. */
  keyNotDeferrable(DatabaseMetaData.importedKeyNotDeferrable, "not deferrable");

  private static final Logger LOGGER = Logger
    .getLogger(ForeignKeyDeferrability.class.getName());

  /**
   * Gets the enum value from the integer.
   * 
   * @param id
   *        Id of the integer
   * @return ForeignKeyDeferrability
   */
  public static ForeignKeyDeferrability valueOf(final int id)
  {
    for (final ForeignKeyDeferrability fkDeferrability: ForeignKeyDeferrability
      .values())
    {
      if (fkDeferrability.getId() == id)
      {
        return fkDeferrability;
      }
    }
    LOGGER.log(Level.FINE, "Unknown id " + id);
    return unknown;
  }

  private final int id;
  private final String text;

  private ForeignKeyDeferrability(final int id, final String text)
  {
    this.id = id;
    this.text = text;
  }

  /**
   * Gets the id.
   * 
   * @return id
   */
  public int getId()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return text;
  }

}
