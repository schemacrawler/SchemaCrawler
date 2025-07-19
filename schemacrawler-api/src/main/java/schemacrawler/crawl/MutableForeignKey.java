/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
