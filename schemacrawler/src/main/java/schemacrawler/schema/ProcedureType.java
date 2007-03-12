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
 * An enumeration wrapper around JDBC procedure types.
 */
public enum ProcedureType
{

  unknown(0, "<unknown>"),
  procedureResultUnknown(DatabaseMetaData.procedureResultUnknown,
    "result unknown"),
  procedureNoResult(DatabaseMetaData.procedureNoResult, "no result"),
  procedureReturnsResult(DatabaseMetaData.procedureReturnsResult,
    "returns result");

  public static ProcedureType valueOf(final int id)
  {
    final EnumSet<ProcedureType> allOf = EnumSet.allOf(ProcedureType.class);
    for (final ProcedureType type: allOf)
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

  private ProcedureType(final int id, final String name)
  {
    this.id = id;
    this.name = name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.EnumType#getId()
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
