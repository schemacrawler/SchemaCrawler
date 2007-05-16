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


import java.sql.DatabaseMetaData;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An enumeration wrapper around index types.
 */
public enum IndexType
{

  /** Unknown */
  unknown(-1),
  /** Statistic. */
  statistic(DatabaseMetaData.tableIndexStatistic),
  /** Clustered. */
  clustered(DatabaseMetaData.tableIndexClustered),
  /** Hashed. */
  hashed(DatabaseMetaData.tableIndexHashed),
  /** Other. */
  other(DatabaseMetaData.tableIndexOther);

  private static final Logger LOGGER = Logger.getLogger(IndexType.class
    .getName());

  /**
   * Gets the value from the id.
   * 
   * @param id
   *        Id of the enumeration.
   * @return IndexType
   */
  public static IndexType valueOf(final int id)
  {
    final EnumSet<IndexType> allOf = EnumSet.allOf(IndexType.class);
    for (final IndexType type: allOf)
    {
      if (type.getId() == id)
      {
        return type;
      }
    }
    LOGGER.log(Level.FINE, "Unknown id " + id);
    return unknown;
  }

  private final int id;

  private IndexType(final int id)
  {
    this.id = id;
  }

  /**
   * Gets the id.
   * 
   * @return id
   */
  public int getId()
  {
    return id;
  }

}
