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
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.StringFormat;

/**
 * An enumeration wrapper around procedure column types.
 */
public enum FunctionColumnType
    implements RoutineColumnType
{

 /**
  * Unknown.
  */
  unknown(DatabaseMetaData.functionColumnUnknown, "unknown"),
 /**
  * In.
  */
  in(DatabaseMetaData.functionColumnIn, "in"),
 /**
  * In/ out.
  */
  inOut(DatabaseMetaData.functionColumnInOut, "in/ out"),
 /**
  * Out.
  */
  out(DatabaseMetaData.functionColumnOut, "out"),
 /**
  * Return.
  */
  returnValue(DatabaseMetaData.functionColumnResult, "return"),
 /**
  * Return.
  */
  result(DatabaseMetaData.procedureColumnResult, "result");

  private static final Logger LOGGER = Logger
    .getLogger(FunctionColumnType.class.getName());

  /**
   * Gets the enum value from the integer.
   *
   * @param id
   *        Id of the integer
   * @return ForeignKeyDeferrability
   */
  public static FunctionColumnType valueOf(final int id)
  {
    for (final FunctionColumnType type: FunctionColumnType.values())
    {
      if (type.getId() == id)
      {
        return type;
      }
    }
    LOGGER.log(Level.FINE, new StringFormat("Unknown id, %d", id));
    return unknown;
  }

  private final int id;
  private final String text;

  private FunctionColumnType(final int id, final String text)
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
