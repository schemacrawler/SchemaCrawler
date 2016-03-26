/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
