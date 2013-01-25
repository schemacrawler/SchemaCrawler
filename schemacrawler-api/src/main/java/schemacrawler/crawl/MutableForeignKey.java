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
import java.util.SortedSet;
import java.util.TreeSet;

import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.NamedObject;
import schemacrawler.utility.CompareUtility;

/**
 * Represents a foreign-key mapping to a primary key in another table.
 * 
 * @author Sualeh Fatehi
 */
class MutableForeignKey
  extends AbstractNamedObject
  implements ForeignKey
{

  private static final long serialVersionUID = 4121411795974895671L;

  private final SortedSet<MutableForeignKeyColumnReference> columnReferences = new TreeSet<MutableForeignKeyColumnReference>();
  private ForeignKeyUpdateRule updateRule;
  private ForeignKeyUpdateRule deleteRule;
  private ForeignKeyDeferrability deferrability;

  MutableForeignKey(final String name)
  {
    super(name);

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
    if (obj == null)
    {
      return -1;
    }

    final ForeignKey other = (ForeignKey) obj;
    final List<ForeignKeyColumnReference> thisColumnReferences = getColumnReferences();
    final List<ForeignKeyColumnReference> otherColumnReferences = other
      .getColumnReferences();

    return CompareUtility.compareLists(thisColumnReferences,
                                       otherColumnReferences);
  }

  /**
   * {@inheritDoc}
   * 
   * @see ForeignKey#getColumnReferences()
   */
  @Override
  public List<ForeignKeyColumnReference> getColumnReferences()
  {
    return new ArrayList<ForeignKeyColumnReference>(columnReferences);
  }

  /**
   * {@inheritDoc}
   * 
   * @see ForeignKey#getDeferrability()
   */
  @Override
  public final ForeignKeyDeferrability getDeferrability()
  {
    return deferrability;
  }

  /**
   * {@inheritDoc}
   * 
   * @see ForeignKey#getDeleteRule()
   */
  @Override
  public final ForeignKeyUpdateRule getDeleteRule()
  {
    return deleteRule;
  }

  /**
   * {@inheritDoc}
   * 
   * @see ForeignKey#getUpdateRule()
   */
  @Override
  public final ForeignKeyUpdateRule getUpdateRule()
  {
    return updateRule;
  }

  void addColumnReference(final int keySequence,
                          final Column pkColumn,
                          final Column fkColumn)
  {
    final MutableForeignKeyColumnReference fkColumnReference = new MutableForeignKeyColumnReference();
    fkColumnReference.setKeySequence(keySequence);
    fkColumnReference.setPrimaryKeyColumn(pkColumn);
    fkColumnReference.setForeignKeyColumn(fkColumn);
    columnReferences.add(fkColumnReference);
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
