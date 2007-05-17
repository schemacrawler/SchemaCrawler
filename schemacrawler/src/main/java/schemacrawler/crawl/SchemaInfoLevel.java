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

package schemacrawler.crawl;


/**
 * Enumeration for level of schema detail.
 */
public enum SchemaInfoLevel
{

  /** No schema detail. */
  minimum,
  /** Basic schema detail. */
  basic,
  /** Verbose schema detail. */
  verbose,
  /** Maximum schema detail. */
  maximum;

  /**
   * Checks if this is greater than the provided info level.
   * 
   * @param infoLevel
   *        Info level to check against
   * @return Yes if this is greater
   */
  public boolean isGreaterThan(final SchemaInfoLevel infoLevel)
  {
    if (infoLevel != null)
    {
      return ordinal() > infoLevel.ordinal();
    }
    else
    {
      return false;
    }
  }

  /**
   * Checks if this is greater than or equal to the provided info level.
   * 
   * @param infoLevel
   *        Info level to check against
   * @return Yes if this is greater or equal to
   */
  public boolean isGreaterThanOrEqualTo(final SchemaInfoLevel infoLevel)
  {
    if (infoLevel != null)
    {
      return ordinal() >= infoLevel.ordinal();
    }
    else
    {
      return false;
    }
  }

}
