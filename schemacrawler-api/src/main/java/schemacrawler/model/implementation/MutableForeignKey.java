/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.NotLoadedException;

import java.io.Serial;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.TableConstraintType;

/** Represents a foreign-key mapping to a primary key in another table. */
public final class MutableForeignKey extends AbstractTableReference implements ForeignKey {

  @Serial private static final long serialVersionUID = 4121411795974895671L;

  private ForeignKeyDeferrability deferrability;
  private ForeignKeyUpdateRule deleteRule;
  private ForeignKeyUpdateRule updateRule;

  public MutableForeignKey(final String name, final ColumnReference columnReference) {
    super(name, columnReference);

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

  public void setDeferrability(final ForeignKeyDeferrability deferrability) {
    this.deferrability = deferrability;
  }

  public void setDeleteRule(final ForeignKeyUpdateRule deleteRule) {
    this.deleteRule = deleteRule;
  }

  public void setUpdateRule(final ForeignKeyUpdateRule updateRule) {
    this.updateRule = updateRule;
  }
}
