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
 * Constraint type.
 */
public final class ConditionTimingType
  implements EnumType
{

  private static final long serialVersionUID = -3033092069397416072L;

  public static final ConditionTimingType UNKNOWN = new ConditionTimingType("unknown");
  public static final ConditionTimingType BEFORE = new ConditionTimingType("BEFORE");
  public static final ConditionTimingType INSTEAD_OF = new ConditionTimingType("INSTEAD OF");
  public static final ConditionTimingType AFTER = new ConditionTimingType("AFTER");

  private static final ConditionTimingType[] ALL = {
      UNKNOWN, BEFORE, INSTEAD_OF, AFTER,
  };

  private final int id;
  private final String name;

  private ConditionTimingType(final String typeName)
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
  public static ConditionTimingType valueOf(final String typeString)
  {

    ConditionTimingType checkOptionType = ALL[0];

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

  private static final ConditionTimingType[] VALUES = ALL;

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }

}
