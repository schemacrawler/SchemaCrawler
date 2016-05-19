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
import schemacrawler.schema.TableConstraintType;
import schemacrawler.utility.CompareUtility;

/**
 * Represents a foreign-key mapping to a primary key in another table.
 *
 * @author Sualeh Fatehi
 */
final class MutableForeignKey
  extends AbstractNamedObjectWithAttributes
  implements ForeignKey
{

  private static final long serialVersionUID = 4121411795974895671L;

  private final SortedSet<MutableForeignKeyColumnReference> columnReferences = new TreeSet<>();

  private ForeignKeyUpdateRule updateRule;
  private ForeignKeyUpdateRule deleteRule;
  private ForeignKeyDeferrability deferrability;
  private Boolean deferrable; // initialize to null
  private Boolean initiallyDeferred; // initialize to null
  private final StringBuilder definition;

  MutableForeignKey(final String name)
  {
    super(name);

    definition = new StringBuilder();

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

  @Override
  public TableConstraintType getConstraintType()
  {
    return TableConstraintType.foreign_key;
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
   * @see ForeignKey#getDefinition()
   */
  @Override
  public String getDefinition()
  {
    return definition.toString();
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
  public boolean hasDefinition()
  {
    return definition.length() > 0;
  }

  @Override
  public boolean isDeferrable()
  {
    if (deferrable == null)
    {
      throw new NotLoadedException(this);
    }
    return deferrable;
  }

  @Override
  public boolean isInitiallyDeferred()
  {
    if (initiallyDeferred == null)
    {
      throw new NotLoadedException(this);
    }
    return initiallyDeferred;
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
    if (pkColumn != null && fkColumn != null && pkColumn.equals(fkColumn))
    {
      return;
    }

    final MutableForeignKeyColumnReference fkColumnReference = new MutableForeignKeyColumnReference(keySequence,
                                                                                                    pkColumn,
                                                                                                    fkColumn);
    columnReferences.add(fkColumnReference);
  }

  void appendDefinition(final String definition)
  {
    if (definition != null)
    {
      this.definition.append(definition);
    }
  }

  final void setDeferrability(final ForeignKeyDeferrability deferrability)
  {
    this.deferrability = deferrability;
  }

  void setDeferrable(final boolean deferrable)
  {
    this.deferrable = deferrable;
  }

  final void setDeleteRule(final ForeignKeyUpdateRule deleteRule)
  {
    this.deleteRule = deleteRule;
  }

  void setInitiallyDeferred(final boolean initiallyDeferred)
  {
    this.initiallyDeferred = initiallyDeferred;
  }

  final void setUpdateRule(final ForeignKeyUpdateRule updateRule)
  {
    this.updateRule = updateRule;
  }

}
