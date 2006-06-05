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
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.util.NaturalSortComparator;

/**
 * Represents a foreign-key mapping to a private key in another table.
 */
class MutableForeignKey
  extends AbstractDatabaseObject
  implements ForeignKey
{

  private static final long serialVersionUID = 4121411795974895671L;

  private final NamedObjectList columnPairs = new NamedObjectList(
      new NaturalSortComparator());
  private ForeignKeyUpdateRule updateRule;
  private ForeignKeyUpdateRule deleteRule;
  private ForeignKeyDeferrability deferrability;

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.ForeignKey#getDeferrability()
   */
  public final ForeignKeyDeferrability getDeferrability()
  {
    return deferrability;
  }

  final void setDeferrability(final ForeignKeyDeferrability deferrability)
  {
    this.deferrability = deferrability;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.ForeignKey#getDeleteRule()
   */
  public final ForeignKeyUpdateRule getDeleteRule()
  {
    return deleteRule;
  }

  final void setDeleteRule(final ForeignKeyUpdateRule deleteRule)
  {
    this.deleteRule = deleteRule;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.ForeignKey#getUpdateRule()
   */
  public final ForeignKeyUpdateRule getUpdateRule()
  {
    return updateRule;
  }

  final void setUpdateRule(final ForeignKeyUpdateRule updateRule)
  {
    this.updateRule = updateRule;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.ForeignKey#getColumnPairs()
   */
  public ForeignKeyColumnMap[] getColumnPairs()
  {
    final List allColumnPairs = columnPairs.getAll();
    return (ForeignKeyColumnMap[]) allColumnPairs
      .toArray(new ForeignKeyColumnMap[allColumnPairs.size()]);
  }

  /**
   * @see schemacrawler.crawl.ForeignKey#getColumnPairsList()
   */
  void addColumnPair(final ForeignKeyColumnMap columnPair)
  {
    columnPairs.add(columnPair);
  }

  /**
   * Adds a column pair.
   * 
   * @param keySequence
   *          Foreign key sequence
   * @param pkColumn
   *          Primary key
   * @param fkColumn
   *          Foreign key
   */
  void addColumnPair(final int keySequence, final Column pkColumn,
                     final Column fkColumn)
  {
    final MutableForeignKeyColumnMap fkColumnPair = new MutableForeignKeyColumnMap();
    fkColumnPair.setName(this.getName() + "." + keySequence);
    fkColumnPair.setKeySequence(keySequence);
    fkColumnPair.setPrimaryKeyColumn(pkColumn);
    fkColumnPair.setForeignKeyColumn(fkColumn);
    addColumnPair(fkColumnPair);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.AbstractNamedObject#compareTo(java.lang.Object)
   *      Note: Since foreign keys are not always explicitly named in databases,
   *      the sorting routine orders the foreign keys by the names of the
   *      columns in the foreign keys.
   */
  public int compareTo(final Object obj)
  {
    final ForeignKey other = (ForeignKey) obj;
    int comparison = 0;
    final ForeignKeyColumnMap[] thisColumnPairs = this.getColumnPairs();
    final ForeignKeyColumnMap[] otherColumnPairs = other.getColumnPairs();

    if (comparison == 0)
    {
      comparison = thisColumnPairs.length - otherColumnPairs.length;
    }

    for (int i = 0; i < thisColumnPairs.length; i++)
    {
      final ForeignKeyColumnMap thisColumnPair = thisColumnPairs[i];
      final ForeignKeyColumnMap otherColumnPair = otherColumnPairs[i];
      if (comparison == 0)
      {
        comparison = thisColumnPair.compareTo(otherColumnPair);
      }
      else
      {
        break;
      }
    }

    return comparison;
  }

}
