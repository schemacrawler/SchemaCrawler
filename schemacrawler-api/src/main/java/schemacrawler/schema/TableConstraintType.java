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


import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.StringFormat;

/**
 * Table constraint type.
 */
public enum TableConstraintType
{

 unknown("unknown"),
 primary_key("PRIMARY KEY"),
 foreign_key("FOREIGN KEY"),
 unique("UNIQUE"),
 check("CHECK"),;

  private static final Logger LOGGER = Logger
    .getLogger(TableConstraintType.class.getName());

  /**
   * Find the enumeration value corresponding to the string.
   *
   * @param value
   *        Sort sequence code.
   * @return Enumeration value
   */
  public static TableConstraintType valueOfFromValue(final String value)
  {
    for (final TableConstraintType type: TableConstraintType.values())
    {
      if (type.getValue().equalsIgnoreCase(value))
      {
        return type;
      }
    }
    LOGGER.log(Level.FINE, new StringFormat("Unknown value, %s", value));
    return unknown;
  }

  private final String value;

  private TableConstraintType(final String value)
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
