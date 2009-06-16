/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;

/**
 * Represents a foreign-key mapping to a primary key in another table.
 * 
 * @author Sualeh Fatehi
 */
class MutableForeignKey
  extends AbstractDatabaseObject
  implements ForeignKey
{

  private static final long serialVersionUID = 4121411795974895671L;

  private final NamedObjectList<MutableForeignKeyColumnMap> columnPairs = new NamedObjectList<MutableForeignKeyColumnMap>(NamedObjectSort.natural);
  private ForeignKeyUpdateRule updateRule;
  private ForeignKeyUpdateRule deleteRule;
  private ForeignKeyDeferrability deferrability;

  MutableForeignKey(final Schema schema, final String name)
  {
    super(schema, name);

    // Default values
    updateRule = ForeignKeyUpdateRule.unknown;
    deleteRule = ForeignKeyUpdateRule.unknown;
    deferrability = ForeignKeyDeferrability.unknown;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Note: Since foreign keys are not always explicitly named in
   * databases, the sorting routine orders the foreign keys by the names
   * of the columns in the foreign keys.
   * </p>
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    final ForeignKey other = (ForeignKey) obj;
    int comparison = 0;
    final ForeignKeyColumnMap[] thisColumnPairs = getColumnPairs();
    final ForeignKeyColumnMap[] otherColumnPairs = other.getColumnPairs();

    if (comparison == 0)
    {
      comparison = thisColumnPairs.length - otherColumnPairs.length;
    }

    if (comparison == 0)
    {
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
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   * 
   * @see ForeignKey#getColumnPairs()
   */
  public ForeignKeyColumnMap[] getColumnPairs()
  {
    return columnPairs.values().toArray(new ForeignKeyColumnMap[columnPairs
      .size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see ForeignKey#getDeferrability()
   */
  public final ForeignKeyDeferrability getDeferrability()
  {
    return deferrability;
  }

  /**
   * {@inheritDoc}
   * 
   * @see ForeignKey#getDeleteRule()
   */
  public final ForeignKeyUpdateRule getDeleteRule()
  {
    return deleteRule;
  }

  /**
   * {@inheritDoc}
   * 
   * @see ForeignKey#getUpdateRule()
   */
  public final ForeignKeyUpdateRule getUpdateRule()
  {
    return updateRule;
  }

  void addColumnPair(final int keySequence,
                     final Column pkColumn,
                     final Column fkColumn)
  {
    final String fkColumnMapName = getName() + "." + keySequence;
    final MutableForeignKeyColumnMap fkColumnPair = new MutableForeignKeyColumnMap(this,
                                                                                   fkColumnMapName);
    fkColumnPair.setKeySequence(keySequence);
    fkColumnPair.setPrimaryKeyColumn(pkColumn);
    fkColumnPair.setForeignKeyColumn(fkColumn);
    addColumnPair(fkColumnPair);
  }

  void addColumnPair(final MutableForeignKeyColumnMap columnPair)
  {
    columnPairs.add(columnPair);
  }

  final void setDeferrability(final ForeignKeyDeferrability deferrability)
  {
    this.deferrability = deferrability;
  }

  final void setDeleteRule(final ForeignKeyUpdateRule deleteRule)
  {
    this.deleteRule = deleteRule;
  }

  final void setUpdateRule(final ForeignKeyUpdateRule updateRule)
  {
    this.updateRule = updateRule;
  }

}
