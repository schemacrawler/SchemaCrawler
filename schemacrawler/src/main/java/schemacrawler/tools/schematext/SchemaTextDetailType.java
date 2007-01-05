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

package schemacrawler.tools.schematext;


import java.io.ObjectStreamException;
import java.io.Serializable;

import schemacrawler.crawl.SchemaInfoLevel;

/**
 * Enumeration for level of column detail.
 */
public final class SchemaTextDetailType
  implements Serializable
{

  private static final long serialVersionUID = 6740850596238696478L;

  /**
   * No column detail.
   */
  public static final SchemaTextDetailType BRIEF = new SchemaTextDetailType(0,
                                                                            "brief_schema");

  /**
   * Basic column detail.
   */
  public static final SchemaTextDetailType BASIC = new SchemaTextDetailType(1,
                                                                            "basic_schema");

  /**
   * Verbose column detail, without table and column numbers.
   */
  public static final SchemaTextDetailType VERBOSE = new SchemaTextDetailType(2,
                                                                              "verbose_schema");

  /**
   * Verbose column detail, without table and column numbers.
   */
  public static final SchemaTextDetailType MAXIMUM = new SchemaTextDetailType(3,
                                                                              "maximum_schema");

  private static final SchemaTextDetailType[] TEXT_FORMAT_TYPE_ALL = {
      BRIEF, BASIC, VERBOSE, MAXIMUM,
  };

  private final transient int id;
  private final transient String name;

  private SchemaTextDetailType(final int id, final String name)
  {
    ordinal = nextOrdinal++;
    this.id = id;
    this.name = name;
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
   * Checks if this is greater than the provided info level.
   * 
   * @param schemaTextDetailType
   *        SchemaTextDetailType to check against
   * @return Yes if this is greater
   */
  public boolean isGreaterThan(final SchemaTextDetailType schemaTextDetailType)
  {
    return id > schemaTextDetailType.id;
  }

  /**
   * Checks if this is greater than or equal to the provided info level.
   * 
   * @param schemaTextDetailType
   *        SchemaTextDetailType to check against
   * @return Yes if this is greater or equal to
   */
  public boolean isGreaterThanOrEqualTo(final SchemaTextDetailType schemaTextDetailType)
  {
    return id >= schemaTextDetailType.id;
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param type
   *        String value of table type
   * @return Enumeration value
   */
  public static SchemaTextDetailType valueOf(final String type)
  {

    SchemaTextDetailType columnInfoLevel = null;

    for (int i = 0; i < TEXT_FORMAT_TYPE_ALL.length; i++)
    {
      if (TEXT_FORMAT_TYPE_ALL[i].toString().equalsIgnoreCase(type))
      {
        columnInfoLevel = TEXT_FORMAT_TYPE_ALL[i];
        break;
      }
    }

    return columnInfoLevel;

  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @return Enumeration value
   */
  public SchemaInfoLevel mapToInfoLevel()
  {

    SchemaInfoLevel columnInfoLevel = SchemaInfoLevel.BASIC;

    if (this == BRIEF)
    {
      columnInfoLevel = SchemaInfoLevel.MINIMUM;
    }
    else if (this == BASIC)
    {
      columnInfoLevel = SchemaInfoLevel.BASIC;
    }
    else if (this == VERBOSE)
    {
      columnInfoLevel = SchemaInfoLevel.VERBOSE;
    }
    else if (this == MAXIMUM)
    {
      columnInfoLevel = SchemaInfoLevel.MAXIMUM;
    }

    return columnInfoLevel;

  }

  // The 4 declarations below are necessary for serialization
  private static int nextOrdinal;
  private final int ordinal;

  private static final SchemaTextDetailType[] VALUES = TEXT_FORMAT_TYPE_ALL;

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }

}
