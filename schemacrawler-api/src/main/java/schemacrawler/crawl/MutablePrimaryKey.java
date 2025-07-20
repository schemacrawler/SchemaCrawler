/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static schemacrawler.schema.TableConstraintType.alternate_key;
import static schemacrawler.schema.TableConstraintType.primary_key;
import java.util.EnumSet;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintType;

/** Represents a primary key in a table. */
final class MutablePrimaryKey extends MutableTableConstraint implements PrimaryKey {

  private static final long serialVersionUID = -7169206178562782087L;

  static MutablePrimaryKey newAlternateKey(final Table parent, final String name) {
    return new MutablePrimaryKey(parent, name, alternate_key);
  }

  static MutablePrimaryKey newPrimaryKey(final MutableTable parent, final String name) {
    return new MutablePrimaryKey(parent, name, primary_key);
  }

  private final TableConstraintType type;

  private MutablePrimaryKey(final Table parent, final String name, final TableConstraintType type) {
    super(parent, name);
    if (!EnumSet.of(alternate_key, primary_key).contains(type)) {
      throw new IllegalArgumentException("Incorrect table constraint type provided");
    }
    this.type = type;
  }

  @Override
  public TableConstraintType getType() {
    return type;
  }

  @Override
  public boolean isDeferrable() {
    return false;
  }

  @Override
  public boolean isInitiallyDeferred() {
    return false;
  }
}
