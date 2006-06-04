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

package schemacrawler.crawl;


/**
 * Enumeration for level of schema detail.
 */
public final class SchemaInfoLevel
{

  /**
   * No schema detail.
   */
  public static final SchemaInfoLevel MINIMUM = new SchemaInfoLevel(0, "NONE");

  /**
   * Basic schema detail.
   */
  public static final SchemaInfoLevel BASIC = new SchemaInfoLevel(1, "BASIC");

  /**
   * Verbose schema detail.
   */
  public static final SchemaInfoLevel VERBOSE = new SchemaInfoLevel(2,
      "VERBOSE");

  /**
   * Maximum schema detail.
   */
  public static final SchemaInfoLevel MAXIMUM = new SchemaInfoLevel(3,
      "MAXIMUM");

  private static final SchemaInfoLevel[] SCHEMA_INFO_LEVEL_ALL = {
    MINIMUM, BASIC, VERBOSE, MAXIMUM
  };

  private final int infoLevelId;
  private final String infoLevelName;

  private SchemaInfoLevel(final int infoLevelId, final String infoLevelName)
  {
    this.infoLevelId = infoLevelId;
    this.infoLevelName = infoLevelName;
  }

  private int getInfoLevelId()
  {
    return infoLevelId;
  }

  /**
   * Checks if this is greater than the provided info level.
   * 
   * @param infoLevel
   *          Info level to check against
   * @return Yes if this is greater
   */
  public boolean isGreaterThan(final SchemaInfoLevel infoLevel)
  {
    return this.getInfoLevelId() > infoLevel.getInfoLevelId();
  }

  /**
   * Checks if this is greater than or equal to the provided info level.
   * 
   * @param infoLevel
   *          Info level to check against
   * @return Yes if this is greater or equal to
   */
  public boolean isGreaterThanOrEqualTo(final SchemaInfoLevel infoLevel)
  {
    return this.getInfoLevelId() >= infoLevel.getInfoLevelId();
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    return infoLevelName;
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param infoLevelString
   *          String value of table type
   * @return Enumeration value
   */
  public static SchemaInfoLevel valueOf(final String infoLevelString)
  {

    SchemaInfoLevel schemaInfoLevel = null;

    for (int i = 0; i < SCHEMA_INFO_LEVEL_ALL.length; i++)
    {
      if (SCHEMA_INFO_LEVEL_ALL[i].toString().equalsIgnoreCase(infoLevelString))
      {
        schemaInfoLevel = SCHEMA_INFO_LEVEL_ALL[i];
        break;
      }
    }

    return schemaInfoLevel;

  }

}
