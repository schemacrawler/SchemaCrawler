/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKeyColumnReference;

/**
 * Represents a single column mapping from a primary key column to a
 * foreign key column.
 *
 * @author Sualeh Fatehi
 */
final class MutableForeignKeyColumnReference
  extends BaseColumnReference
  implements ForeignKeyColumnReference
{

  private static final long serialVersionUID = 3689073962672273464L;

  private final int keySequence;

  MutableForeignKeyColumnReference(final int keySequence,
                                   final Column primaryKeyColumn,
                                   final Column foreignKeyColumn)
  {
    super(primaryKeyColumn, foreignKeyColumn);
    this.keySequence = keySequence;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ColumnReference other1)
  {
    int comparison = 0;

    final ForeignKeyColumnReference other = (ForeignKeyColumnReference) other1;
    if (comparison == 0)
    {
      comparison = getKeySequence() - other.getKeySequence();
    }
    if (comparison == 0)
    {
      comparison = super.compareTo(other1);
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ForeignKeyColumnReference#getKeySequence()
   */
  @Override
  public int getKeySequence()
  {
    return keySequence;
  }

}
