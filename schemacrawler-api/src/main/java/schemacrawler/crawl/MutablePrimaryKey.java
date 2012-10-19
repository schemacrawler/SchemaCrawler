/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;

/**
 * Represents a primary key in a table.
 * 
 * @author Sualeh Fatehi
 */
class MutablePrimaryKey
  extends MutableIndex
  implements PrimaryKey
{

  private static final long serialVersionUID = -7169206178562782087L;

  /**
   * Copies information from an index.
   * 
   * @param index
   *        Index
   */
  MutablePrimaryKey(final MutableIndex index)
  {
    super(index.getParent(), index.getName());
    setCardinality(index.getCardinality());
    setPages(index.getPages());
    setRemarks(index.getRemarks());
    setIndexType(index.getIndexType());
    setUnique(index.isUnique());
    // Copy columns
    for (final IndexColumn column: index.getColumns())
    {
      addColumn((MutableIndexColumn) column);
    }
  }

  MutablePrimaryKey(final Table parent, final String name)
  {
    super(parent, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#isUnique()
   */
  @Override
  public final boolean isUnique()
  {
    return true;
  }

}
