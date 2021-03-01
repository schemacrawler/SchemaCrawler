/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.TableConstraintType;
import us.fatehi.utility.CompareUtility;

/**
 * Represents a foreign-key mapping to a primary key in another table.
 *
 * @author Sualeh Fatehi
 */
final class MutableForeignKey extends AbstractNamedObjectWithAttributes implements ForeignKey {

  private static final long serialVersionUID = 4121411795974895671L;

  private final String specificName;
  private transient NamedObjectKey key;
  private final SortedSet<MutableForeignKeyColumnReference> columnReferences = new TreeSet<>();
  private final StringBuilder definition;
  private ForeignKeyDeferrability deferrability;
  private ForeignKeyUpdateRule deleteRule;
  private ForeignKeyUpdateRule updateRule;

  MutableForeignKey(final String name, final String specificName) {
    super(name);
    this.specificName = specificName;

    definition = new StringBuilder();

    // Default values
    updateRule = ForeignKeyUpdateRule.unknown;
    deleteRule = ForeignKeyUpdateRule.unknown;
    deferrability = ForeignKeyDeferrability.unknown;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Note: Since foreign keys are not always explicitly named in databases, the sorting routine
   * orders the foreign keys by the names of the columns in the foreign keys.
   */
  @Override
  public int compareTo(final NamedObject obj) {
    if (obj == null) {
      return -1;
    }

    final BaseForeignKey<?> other = (BaseForeignKey<?>) obj;
    final List<? extends ColumnReference> thisColumnReferences = getColumnReferences();
    final List<? extends ColumnReference> otherColumnReferences = other.getColumnReferences();

    return CompareUtility.compareLists(thisColumnReferences, otherColumnReferences);
  }

  /** {@inheritDoc} */
  @Override
  public List<ForeignKeyColumnReference> getColumnReferences() {
    return new ArrayList<>(columnReferences);
  }

  @Override
  public TableConstraintType getConstraintType() {
    return TableConstraintType.foreign_key;
  }

  /** {@inheritDoc} */
  @Override
  public ForeignKeyDeferrability getDeferrability() {
    return deferrability;
  }

  /** {@inheritDoc} */
  @Override
  public String getDefinition() {
    return definition.toString();
  }

  /** {@inheritDoc} */
  @Override
  public ForeignKeyUpdateRule getDeleteRule() {
    return deleteRule;
  }

  @Override
  public String getSpecificName() {
    return specificName;
  }

  /** {@inheritDoc} */
  @Override
  public ForeignKeyUpdateRule getUpdateRule() {
    return updateRule;
  }

  @Override
  public boolean hasDefinition() {
    return definition.length() > 0;
  }

  @Override
  public boolean isDeferrable() {
    return isInitiallyDeferred();
  }

  @Override
  public boolean isInitiallyDeferred() {
    if (deferrability == null) {
      throw new NotLoadedException(this);
    }
    return deferrability == ForeignKeyDeferrability.initiallyDeferred;
  }

  @Override
  public Iterator<ForeignKeyColumnReference> iterator() {
    return new ArrayList<ForeignKeyColumnReference>(columnReferences).iterator();
  }

  @Override
  public NamedObjectKey key() {
    buildKey();
    return key;
  }

  void addColumnReference(final int keySequence, final Column pkColumn, final Column fkColumn) {
    final MutableForeignKeyColumnReference fkColumnReference =
        new MutableForeignKeyColumnReference(keySequence, pkColumn, fkColumn);
    columnReferences.add(fkColumnReference);
  }

  void appendDefinition(final String definition) {
    if (definition != null) {
      this.definition.append(definition);
    }
  }

  void setDeferrability(final ForeignKeyDeferrability deferrability) {
    this.deferrability = deferrability;
  }

  void setDeleteRule(final ForeignKeyUpdateRule deleteRule) {
    this.deleteRule = deleteRule;
  }

  void setUpdateRule(final ForeignKeyUpdateRule updateRule) {
    this.updateRule = updateRule;
  }

  private void buildKey() {
    if (key != null) {
      return;
    }
    this.key = new NamedObjectKey(getName(), specificName);
  }
}
