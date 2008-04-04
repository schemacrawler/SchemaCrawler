/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.NamedObject;

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

  MutableForeignKey(final String catalogName,
                    final String schemaName,
                    final String name)
  {
    super(catalogName, schemaName, name);

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
    return columnPairs.getAll().toArray(new ForeignKeyColumnMap[columnPairs
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
    final MutableForeignKeyColumnMap fkColumnPair = new MutableForeignKeyColumnMap(fkColumnMapName,
                                                                                   this);
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
