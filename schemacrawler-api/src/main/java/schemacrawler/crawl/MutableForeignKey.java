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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import schemacrawler.schema.BaseForeignKey;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
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
  extends AbstractNamedObjectWithAttributes
  implements ForeignKey
{

  private static final long serialVersionUID = 4121411795974895671L;

  private final SortedSet<MutableForeignKeyColumnReference> columnReferences = new TreeSet<>();
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

    final BaseForeignKey<?> other = (BaseForeignKey<?>) obj;
    final List<? extends ColumnReference> thisColumnReferences = getColumnReferences();
    final List<? extends ColumnReference> otherColumnReferences = other
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

  @Override
  public Iterator<ForeignKeyColumnReference> iterator()
  {
    return new ArrayList<ForeignKeyColumnReference>(columnReferences)
      .iterator();
  }

  void addColumnReference(final int keySequence,
                          final Column pkColumn,
                          final Column fkColumn)
  {
    final MutableForeignKeyColumnReference fkColumnReference = new MutableForeignKeyColumnReference(keySequence,
                                                                                                    pkColumn,
                                                                                                    fkColumn);
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
