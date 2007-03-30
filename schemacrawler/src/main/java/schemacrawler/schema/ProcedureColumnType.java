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

/**
 * An enumeration wrapper around procedure column types.
 */
public enum ProcedureColumnType
{

  /** Unknown. */
  procedureColumnUnknown(DatabaseMetaData.procedureColumnUnknown, "?"),
  /** In. */
  procedureColumnIn(DatabaseMetaData.procedureColumnIn, "in"),
  /** In/ out. */
  procedureColumnInOut(DatabaseMetaData.procedureColumnInOut, "in/ out"),
  /** Out. */
  procedureColumnOut(DatabaseMetaData.procedureColumnOut, "out"),
  /** Return. */
  procedureColumnReturn(DatabaseMetaData.procedureColumnReturn, "return"),
  /** Return. */
  procedureColumnResult(DatabaseMetaData.procedureColumnResult, "result");

  /**
   * Gets the enum value from the integer.
   * 
   * @param id
   *        Id of the integer
   * @return ForeignKeyDeferrability
   */
  public static ProcedureColumnType valueOf(final int id)
  {
    final EnumSet<ProcedureColumnType> allOf = EnumSet
      .allOf(ProcedureColumnType.class);
    for (final ProcedureColumnType type: allOf)
    {
      if (type.getId() == id)
      {
        return type;
      }
    }
    return null;
  }

  private final int id;
  private final String name;

  private ProcedureColumnType(final int id, final String name)
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
