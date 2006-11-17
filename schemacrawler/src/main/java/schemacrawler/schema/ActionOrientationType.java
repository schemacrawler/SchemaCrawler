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

/**
 * Action orientation type.
 */
public final class ActionOrientationType
  implements EnumType
{

  private static final long serialVersionUID = 4767973714560552564L;

  /** Unknown */
  public static final ActionOrientationType UNKNOWN = new ActionOrientationType("unknown");
  /** Row */
  public static final ActionOrientationType ROW = new ActionOrientationType("ROW");
  /** Statement */
  public static final ActionOrientationType STATEMENT = new ActionOrientationType("STATEMENT");

  private static final ActionOrientationType[] ALL = {
      UNKNOWN, ROW, STATEMENT,
  };

  private final int id;
  private final String name;

  private ActionOrientationType(final String typeName)
  {
    ordinal = nextOrdinal++;
    id = ordinal;
    name = typeName;
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
   * @see schemacrawler.schema.EnumType#getName()
   */
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
    return name;
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param typeString
   *        String value of table type
   * @return Enumeration value
   */
  public static ActionOrientationType valueOf(final String typeString)
  {

    ActionOrientationType checkOptionType = ALL[0];

    for (int i = 0; i < ALL.length; i++)
    {
      if (ALL[i].toString().equalsIgnoreCase(typeString))
      {
        checkOptionType = ALL[i];
        break;
      }
    }

    return checkOptionType;

  }

  // The 4 declarations below are necessary for serialization
  private static int nextOrdinal;
  private final int ordinal;

  private static final ActionOrientationType[] VALUES = ALL;

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }

}
