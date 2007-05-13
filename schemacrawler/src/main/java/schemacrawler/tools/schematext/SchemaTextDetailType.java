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


import java.util.EnumSet;

import schemacrawler.crawl.SchemaInfoLevel;

/**
 * Enumeration for level of schema text output detail.
 */
public enum SchemaTextDetailType
{

  /** No column detail. */
  BRIEF("brief_schema"),
  /** Basic column detail. */
  BASIC("basic_schema"),
  /** Verbose column detail. */
  VERBOSE("verbose_schema"),
  /** Maximum column detail, everything supported by SchemaCrawler. */
  MAXIMUM("maximum_schema");

  private final String command;

  private SchemaTextDetailType(final String command)
  {
    this.command = command;
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
    return ordinal() >= schemaTextDetailType.ordinal();
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @return Enumeration value
   */
  public SchemaInfoLevel mapToInfoLevel()
  {
    SchemaInfoLevel columnInfoLevel;
    switch (this)
    {
      case BRIEF:
        columnInfoLevel = SchemaInfoLevel.MINIMUM;
        break;
      case BASIC:
        columnInfoLevel = SchemaInfoLevel.BASIC;
        break;
      case VERBOSE:
        columnInfoLevel = SchemaInfoLevel.VERBOSE;
        break;
      case MAXIMUM:
        columnInfoLevel = SchemaInfoLevel.MAXIMUM;
        break;
      default:
        columnInfoLevel = SchemaInfoLevel.BASIC;
        break;
    }
    return columnInfoLevel;
  }

  /**
   * Gets the enum value from the integer.
   * 
   * @param command
   *        Command
   * @return IndexType
   */
  public static SchemaTextDetailType fromCommand(final String command)
  {
    final EnumSet<SchemaTextDetailType> allOf = EnumSet
      .allOf(SchemaTextDetailType.class);
    for (final SchemaTextDetailType type: allOf)
    {
      if (type.getCommand().equals(command))
      {
        return type;
      }
    }
    return null;
  }

  public String getCommand()
  {
    return command;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return command;
  }

}
