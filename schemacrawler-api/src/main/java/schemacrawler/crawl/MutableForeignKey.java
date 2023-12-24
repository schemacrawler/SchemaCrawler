/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.TableConstraintType;

/** Represents a foreign-key mapping to a primary key in another table. */
final class MutableForeignKey extends AbstractTableReference implements ForeignKey {

  private static final long serialVersionUID = 4121411795974895671L;

  private final StringBuffer definition;
  private ForeignKeyDeferrability deferrability;
  private ForeignKeyUpdateRule deleteRule;
  private ForeignKeyUpdateRule updateRule;

  MutableForeignKey(final String name, final ColumnReference columnReference) {
    super(name, columnReference);

    definition = new StringBuffer();

    // Default values
    updateRule = ForeignKeyUpdateRule.unknown;
    deleteRule = ForeignKeyUpdateRule.unknown;
    deferrability = ForeignKeyDeferrability.unknown;
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
  public TableConstraintType getType() {
    return TableConstraintType.foreign_key;
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
}
