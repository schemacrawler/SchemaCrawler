/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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


import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.NamedObject;

/**
 * Represents a single column mapping from a primary key column to a
 * foreign key column.
 * 
 * @author Sualeh Fatehi
 */
final class MutableForeignKeyColumnMap
  extends AbstractDependantNamedObject
  implements ForeignKeyColumnMap
{

  private static final long serialVersionUID = 3689073962672273464L;

  private Column foreignKeyColumn;
  private Column primaryKeyColumn;
  private int keySequence;

  MutableForeignKeyColumnMap(final NamedObject parent, final String name)
  {
    super(parent, name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    if (obj == null)
    {
      return -1;
    }

    final ForeignKeyColumnMap other = (ForeignKeyColumnMap) obj;
    int comparison = 0;

    if (comparison == 0)
    {
      comparison = getKeySequence() - other.getKeySequence();
    }
    // Note: For the primary key and foreign key columns, compare by
    // name.
    if (comparison == 0)
    {
      comparison = getPrimaryKeyColumn().getFullName().compareTo(other
        .getPrimaryKeyColumn().getFullName());
    }
    if (comparison == 0)
    {
      comparison = getForeignKeyColumn().getFullName().compareTo(other
        .getForeignKeyColumn().getFullName());
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnMap#getForeignKeyColumn()
   */
  @Override
  public Column getForeignKeyColumn()
  {
    return foreignKeyColumn;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnMap#getKeySequence()
   */
  @Override
  public int getKeySequence()
  {
    return keySequence;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnMap#getPrimaryKeyColumn()
   */
  @Override
  public Column getPrimaryKeyColumn()
  {
    return primaryKeyColumn;
  }

  void setForeignKeyColumn(final Column foreignKeyColumn)
  {
    this.foreignKeyColumn = foreignKeyColumn;
  }

  void setKeySequence(final int keySequence)
  {
    this.keySequence = keySequence;
  }

  void setPrimaryKeyColumn(final Column primaryKeyColumn)
  {
    this.primaryKeyColumn = primaryKeyColumn;
  }

}
