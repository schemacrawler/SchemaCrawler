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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Foreign key update and delete rules.
 */
public enum ForeignKeyUpdateRule
{

  /** Unknown */
  unknown(-1, "unknown"),
  /** No action. */
  noAction(DatabaseMetaData.importedKeyNoAction, "no action"),
  /** Cascade. */
  cascade(DatabaseMetaData.importedKeyCascade, "cascade"),
  /** Set null. */
  setNull(DatabaseMetaData.importedKeySetNull, "set null"),
  /** Set default. */
  setDefault(DatabaseMetaData.importedKeySetDefault, "set default"),
  /** Restrict. */
  restrict(DatabaseMetaData.importedKeyRestrict, "restrict");

  private static final Logger LOGGER = Logger
    .getLogger(ForeignKeyUpdateRule.class.getName());

  /**
   * Gets the enum value from the integer.
   * 
   * @param id
   *        Id of the integer
   * @return ForeignKeyUpdateRule
   */
  public static ForeignKeyUpdateRule valueOf(final int id)
  {
    for (final ForeignKeyUpdateRule type: ForeignKeyUpdateRule.values())
    {
      if (type.getId() == id)
      {
        return type;
      }
    }
    LOGGER.log(Level.FINE, "Unknown id " + id);
    return unknown;
  }

  private final String text;
  private final int id;

  private ForeignKeyUpdateRule(final int id,
                               final String text)
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
