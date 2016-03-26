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
 * Foreign key update and delete rules.
 */
public enum ForeignKeyUpdateRule
  implements IdentifiedEnum
{

 /**
  * Unknown
  */
 unknown(-1, "unknown"),
 /**
  * No action.
  */
 noAction(DatabaseMetaData.importedKeyNoAction, "no action"),
 /**
  * Cascade.
  */
 cascade(DatabaseMetaData.importedKeyCascade, "cascade"),
 /**
  * Set null.
  */
 setNull(DatabaseMetaData.importedKeySetNull, "set null"),
 /**
  * Set default.
  */
 setDefault(DatabaseMetaData.importedKeySetDefault, "set default"),
 /**
  * Restrict.
  */
 restrict(DatabaseMetaData.importedKeyRestrict, "restrict");

  private final String text;
  private final int id;

  private ForeignKeyUpdateRule(final int id, final String text)
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
