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


import java.io.ObjectStreamException;

/**
 * Routine body.
 */
public final class RoutineBodyType
  implements EnumType
{

  private static final long serialVersionUID = 6162604444140905085L;

  private static final RoutineBodyType SQL = new RoutineBodyType("SQL");
  private static final RoutineBodyType EXTERNAL = new RoutineBodyType("EXTERNAL");

  private static final RoutineBodyType[] ALL = {
      SQL, EXTERNAL,
  };

  // The 4 declarations below are necessary for serialization
  private static int nextOrdinal;
  private static final RoutineBodyType[] VALUES = ALL;

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param typeString
   *        String value of table type
   * @return Enumeration value
   */
  public static RoutineBodyType valueOf(final String typeString)
  {

    RoutineBodyType checkOptionType = ALL[0];

    for (final RoutineBodyType element: ALL)
    {
      if (element.toString().equalsIgnoreCase(typeString))
      {
        checkOptionType = element;
        break;
      }
    }

    return checkOptionType;

  }

  private final int id;

  private final String name;

  private final int ordinal;

  private RoutineBodyType(final String typeName)
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
  @Override
  public String toString()
  {
    return name;
  }

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }
}
