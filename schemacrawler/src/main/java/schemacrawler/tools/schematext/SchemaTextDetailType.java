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


import schemacrawler.crawl.SchemaInfoLevel;

/**
 * Enumeration for level of schema text output detail.
 */
public enum SchemaTextDetailType
{

  /** No column detail. */
  brief_schema,
  /** Basic column detail. */
  basic_schema,
  /** Verbose column detail. */
  verbose_schema,
  /** Maximum column detail, everything supported by SchemaCrawler. */
  maximum_schema;

  /**
   * Checks if this is greater than or equal to the provided info level.
   * 
   * @param schemaTextDetailType
   *        SchemaTextDetailType to check against
   * @return Yes if this is greater or equal to
   */
  public boolean isGreaterThanOrEqualTo(final SchemaTextDetailType schemaTextDetailType)
  {
    if (schemaTextDetailType != null)
    {
      return ordinal() >= schemaTextDetailType.ordinal();
    }
    else
    {
      return false;
    }
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
      case brief_schema:
        columnInfoLevel = SchemaInfoLevel.minimum();
        break;
      case basic_schema:
        columnInfoLevel = SchemaInfoLevel.basic();
        break;
      case verbose_schema:
        columnInfoLevel = SchemaInfoLevel.verbose();
        break;
      case maximum_schema:
        columnInfoLevel = SchemaInfoLevel.maximum();
        break;
      default:
        columnInfoLevel = SchemaInfoLevel.basic();
        break;
    }
    return columnInfoLevel;
  }

}
