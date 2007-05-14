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

package schemacrawler.schema;


import java.sql.DatabaseMetaData;
import java.util.EnumSet;
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
  importedKeyInitiallyDeferred(DatabaseMetaData.importedKeyInitiallyDeferred,
    "initially deferred"),
  /** Initially immediate. */
  importedKeyInitiallyImmediate(DatabaseMetaData.importedKeyInitiallyImmediate,
    "initially immediate"),
  /** Not deferrable. */
  importedKeyNotDeferrable(DatabaseMetaData.importedKeyNotDeferrable,
    "not deferrable");

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
    final EnumSet<ForeignKeyDeferrability> allOf = EnumSet
      .allOf(ForeignKeyDeferrability.class);
    for (final ForeignKeyDeferrability fkDeferrability: allOf)
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
  private final String name;

  private ForeignKeyDeferrability(final int id, final String name)
  {
    this.id = id;
    this.name = name;
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
    return name;
  }

}
