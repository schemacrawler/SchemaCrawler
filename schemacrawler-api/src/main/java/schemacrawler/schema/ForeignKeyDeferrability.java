/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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

import sf.util.IdentifiedEnum;

/**
 * The deferrability value for foreign keys.
 */
public enum ForeignKeyDeferrability
  implements IdentifiedEnum
{

 /**
  * Unknown
  */
 unknown(-1, "unknown"),
 /**
  * Initially deferred.
  */
 initiallyDeferred(DatabaseMetaData.importedKeyInitiallyDeferred, "initially deferred"),
 /**
  * Initially immediate.
  */
 initiallyImmediate(DatabaseMetaData.importedKeyInitiallyImmediate, "initially immediate"),
 /**
  * Not deferrable.
  */
 keyNotDeferrable(DatabaseMetaData.importedKeyNotDeferrable, "not deferrable");

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
  @Override
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
