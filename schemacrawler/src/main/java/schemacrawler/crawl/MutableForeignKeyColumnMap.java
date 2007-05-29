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


import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.NamedObject;

/**
 * {@inheritDoc}
 */
final class MutableForeignKeyColumnMap
  extends AbstractDependantNamedObject
  implements ForeignKeyColumnMap
{

  private static final long serialVersionUID = 3689073962672273464L;

  private Column foreignKeyColumn;
  private Column primaryKeyColumn;
  private int keySequence;

  MutableForeignKeyColumnMap(final String name, final NamedObject parent)
  {
    super(name, parent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    final ForeignKeyColumnMap other = (ForeignKeyColumnMap) obj;
    int comparison = 0;

    if (comparison == 0)
    {
      comparison = getParent().compareTo(other.getParent());
    }
    if (comparison == 0)
    {
      comparison = getPrimaryKeyColumn().compareTo(other.getPrimaryKeyColumn());
    }
    if (comparison == 0)
    {
      comparison = getForeignKeyColumn().compareTo(other.getForeignKeyColumn());
    }
    if (comparison == 0)
    {
      comparison = getKeySequence() - other.getKeySequence();
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnMap#getForeignKeyColumn()
   */
  public Column getForeignKeyColumn()
  {
    return foreignKeyColumn;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnMap#getKeySequence()
   */
  public int getKeySequence()
  {
    return keySequence;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnMap#getPrimaryKeyColumn()
   */
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
