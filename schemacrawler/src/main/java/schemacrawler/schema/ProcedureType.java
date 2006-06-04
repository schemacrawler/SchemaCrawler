/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.io.ObjectStreamException;
import java.sql.DatabaseMetaData;

/**
 * An enumeration wrapper around JDBC procedure types.
 */
public final class ProcedureType
  implements EnumType
{

  private static final long serialVersionUID = 3545517287747366960L;

  private static final ProcedureType[] ALL = {
    new ProcedureType(DatabaseMetaData.procedureResultUnknown, "<unknown>"),
    new ProcedureType(DatabaseMetaData.procedureResultUnknown, "result unknown"),
    new ProcedureType(DatabaseMetaData.procedureNoResult, "no result"),
    new ProcedureType(DatabaseMetaData.procedureReturnsResult, "returns result"),
  };

  private final transient int id;
  private final transient String name;

  private ProcedureType(final int id, final String name)
  {
    ordinal = nextOrdinal++;
    this.id = id;
    this.name = name;
  }

  /**
   * Procedure type id.
   * 
   * @return Returns the procedure type id.
   */
  public int getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    return getName();
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param id
   *          int value of procedure type
   * @return Enumeration value
   */
  public static ProcedureType valueOf(final int id)
  {
    ProcedureType type = ALL[0];
    for (int i = 0; i < ALL.length; i++)
    {
      if (ALL[i].getId() == id)
      {
        type = ALL[i];
        break;
      }
    }
    return type;
  }

  /**
   * Value of the enumeration from the code.
   * 
   * @param name
   *          Code
   * @return Enumeration value
   */
  public static ProcedureType valueOf(final String name)
  {
    ProcedureType type = ALL[0];
    for (int i = 0; i < ALL.length; i++)
    {
      if (ALL[i].getName().equalsIgnoreCase(name))
      {
        type = ALL[i];
        break;
      }
    }
    return type;
  }

  // The 4 declarations below are necessary for serialization
  private static int nextOrdinal;
  private final int ordinal;

  private static final ProcedureType[] VALUES = ALL;

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }

}
