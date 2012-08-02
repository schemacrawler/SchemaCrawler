/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
 * An enumeration wrapper around JDBC procedure types.
 */
public enum ProcedureReturnType
  implements RoutineReturnType
{

  /**
   * Result unknown.
   */
  unknown(DatabaseMetaData.procedureResultUnknown, "result unknown"),
  /**
   * No result.
   */
  noResult(DatabaseMetaData.procedureNoResult, "no result"),
  /**
   * Returns result.
   */
  returnsResult(DatabaseMetaData.procedureReturnsResult, "returns result");

  private static final Logger LOGGER = Logger.getLogger(ProcedureReturnType.class
    .getName());

  /**
   * Gets the enum value from the integer.
   * 
   * @param id
   *        Id of the integer
   * @return ForeignKeyDeferrability
   */
  public static ProcedureReturnType valueOf(final int id)
  {
    for (final ProcedureReturnType type: ProcedureReturnType.values())
    {
      if (type.getId() == id)
      {
        return type;
      }
    }
    LOGGER.log(Level.FINE, "Unknown id " + id);
    return unknown;
  }

  private final int id;
  private final String text;

  private ProcedureReturnType(final int id, final String text)
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
