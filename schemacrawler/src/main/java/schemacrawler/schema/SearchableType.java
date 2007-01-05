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
import java.sql.DatabaseMetaData;

/**
 * An enumeration wrapper around JDBC procedure types.
 */
public final class SearchableType
  implements EnumType
{

  private static final long serialVersionUID = -3030898601085718915L;

  private static final SearchableType[] ALL = {
      new SearchableType(DatabaseMetaData.typePredNone, "not searchable"),
      new SearchableType(DatabaseMetaData.typePredChar,
                         "only searchable with where .. like"),
      new SearchableType(DatabaseMetaData.typePredBasic,
                         "searchable except with where .. like"),
      new SearchableType(DatabaseMetaData.typeSearchable, "searchable"),
  };

  private final transient int id;
  private final transient String name;

  private SearchableType(final int id, final String name)
  {
    ordinal = nextOrdinal++;
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
    return getName();
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param id
   *        int value of type
   * @return Enumeration value
   */
  public static SearchableType valueOf(final int id)
  {
    SearchableType type = ALL[0];
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
   *        Code
   * @return Enumeration value
   */
  public static EnumType valueOf(final String name)
  {
    EnumType type = ALL[0];
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

  private static final SearchableType[] VALUES = ALL;

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }

}
