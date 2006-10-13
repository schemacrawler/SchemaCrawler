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


import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.ConstraintType;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexSortSequence;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.TableConstraint;

/**
 * Represents an index on a database table.
 */
class MutableIndex
  extends AbstractDependantNamedObject
  implements Index
{

  private static final long serialVersionUID = 4051326747138079028L;

  private final NamedObjectList columns = new NamedObjectList(null);
  private boolean isUnique;
  private IndexType type;
  private IndexSortSequence sortSequence;
  private int cardinality;
  private int pages;

  /**
   * {@inheritDoc}
   * 
   * @see Index#getCardinality()
   */
  public final int getCardinality()
  {
    return cardinality;
  }

  final void setCardinality(final int cardinality)
  {
    this.cardinality = cardinality;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#isUnique()
   */
  public final boolean isUnique()
  {
    return isUnique;
  }

  final void setUnique(final boolean unique)
  {
    isUnique = unique;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getPages()
   */
  public final int getPages()
  {
    return pages;
  }

  final void setPages(final int pages)
  {
    this.pages = pages;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getSortSequence()
   */
  public final IndexSortSequence getSortSequence()
  {
    return sortSequence;
  }

  final void setSortSequence(final IndexSortSequence sortSequence)
  {
    this.sortSequence = sortSequence;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getType()
   */
  public final IndexType getType()
  {
    return type;
  }

  final void setType(final IndexType type)
  {
    this.type = type;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Index#getColumns()
   */
  public Column[] getColumns()
  {
    final List allColumns = columns.getAll();
    return (Column[]) allColumns.toArray(new Column[allColumns.size()]);
  }

  /**
   * Adds a column.
   * 
   * @param column
   *        Column
   */
  void addColumn(final Column column)
  {
    columns.add(column);
  }

  /**
   * Adds a column at an ordinal position.
   * 
   * @param ordinalPosition
   *        Oridinal position
   * @param column
   *        Column
   */
  void addColumn(final int ordinalPosition, final Column column)
  {
    columns.add(ordinalPosition, column);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Note: Since indexes are not always explicitly named in databases,
   * the sorting routine orders the indexes by the names of the columns
   * in the index.
   * <p>
   * 
   * @see AbstractNamedObject#compareTo(java.lang.Object)
   */
  public int compareTo(final Object obj)
  {
    final Index other = (Index) obj;
    int comparison = 0;
    final Column[] thisColumns = getColumns();
    final Column[] otherColumns = other.getColumns();

    if (comparison == 0)
    {
      comparison = thisColumns.length - otherColumns.length;
    }

    for (int i = 0; i < thisColumns.length; i++)
    {
      final Column thisColumn = thisColumns[i];
      final Column otherColumn = otherColumns[i];
      if (comparison == 0)
      {
        comparison = thisColumn.compareTo(otherColumn);
      } else
      {
        break;
      }
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   */
  public TableConstraint asTableConstraint()
  {
    if (!isUnique())
    {
      // Non-unique indexes are not constraints
      return null;
    }

    final MutableTableConstraint constraint = new MutableTableConstraint();
    constraint.setName(getName());
    constraint.setParent(getParent());
    constraint.setType(ConstraintType.UNIQUE);

    return constraint;
  }

}
