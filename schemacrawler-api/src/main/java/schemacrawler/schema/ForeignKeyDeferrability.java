/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
