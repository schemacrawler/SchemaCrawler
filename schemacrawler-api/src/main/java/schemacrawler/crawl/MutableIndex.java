/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;
import schemacrawler.utility.CompareUtility;

/**
 * Represents an index on a database table.
 * 
 * @author Sualeh Fatehi
 */
class MutableIndex
  extends AbstractDependantObject<Table>
  implements Index
{

  private static final long serialVersionUID = 4051326747138079028L;

  private final NamedObjectList<MutableIndexColumn> columns = new NamedObjectList<>();
  private boolean isUnique;
  private IndexType indexType;
  private int cardinality;
  private int pages;
  private final StringBuilder definition;

  MutableIndex(final Table parent, final String name)
  {
    super(parent, name);
    // Default values
    indexType = IndexType.unknown;
    definition = new StringBuilder();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Note: Since indexes are not always explicitly named in databases,
   * the sorting routine orders the indexes by the names of the columns
   * in the index.
   * </p>
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    if (obj == null || !(obj instanceof Index))
    {
      return -1;
    }

    final Index that = (Index) obj;

    int compareTo = 0;

    if (compareTo == 0)
    {
      final List<IndexColumn> thisColumns = getColumns();
      final List<IndexColumn> thatColumns = that.getColumns();

      compareTo = CompareUtility.compareLists(thisColumns, thatColumns);
    }

    if (compareTo == 0)
    {
      if (isUnique != that.isUnique())
      {
        compareTo = isUnique? -1: 1;
      }
    }

    if (compareTo == 0)
    {
      compareTo = indexType.ordinal() - that.getIndexType().ordinal();
    }

    if (compareTo == 0)
    {
      compareTo = super.compareTo(obj);
    }

    return compareTo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getCardinality()
   */
  @Override
  public final int getCardinality()
  {
    return cardinality;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getColumns()
   */
  @Override
  public List<IndexColumn> getColumns()
  {
    return new ArrayList<IndexColumn>(columns.values());
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Index#getDefinition()
   */
  @Override
  public String getDefinition()
  {
    return definition.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getIndexType()
   */
  @Override
  public final IndexType getIndexType()
  {
    return indexType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getPages()
   */
  @Override
  public final int getPages()
  {
    return pages;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.TypedObject#getType()
   */
  @Override
  public final IndexType getType()
  {
    return getIndexType();
  }

  @Override
  public boolean hasDefinition()
  {
    return definition.length() > 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#isUnique()
   */
  @Override
  public boolean isUnique()
  {
    return isUnique;
  }

  void addColumn(final MutableIndexColumn column)
  {
    columns.add(column);
  }

  void appendDefinition(final String definition)
  {
    if (definition != null)
    {
      this.definition.append(definition);
    }
  }

  final void setCardinality(final int cardinality)
  {
    this.cardinality = cardinality;
  }

  final void setIndexType(final IndexType indexType)
  {
    if (indexType == null)
    {
      throw new IllegalArgumentException("Null index type");
    }
    this.indexType = indexType;
  }

  final void setPages(final int pages)
  {
    this.pages = pages;
  }

  final void setUnique(final boolean unique)
  {
    isUnique = unique;
  }

}
