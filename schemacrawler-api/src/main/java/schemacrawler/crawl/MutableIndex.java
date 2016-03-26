/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
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
    super(new TableReference(parent), name);
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

  @Override
  public Iterator<IndexColumn> iterator()
  {
    return getColumns().iterator();
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
    this.indexType = requireNonNull(indexType, "Null index type");
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
