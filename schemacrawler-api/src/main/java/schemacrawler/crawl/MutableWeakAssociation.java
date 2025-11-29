/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.io.Serial;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.TableConstraintType;
import schemacrawler.schema.WeakAssociation;

/** Represents a foreign-key mapping to a primary key in another table. */
final class MutableWeakAssociation extends AbstractTableReference implements WeakAssociation {

  @Serial private static final long serialVersionUID = -5164664131926303038L;

  public MutableWeakAssociation(final String name, final ColumnReference columnReference) {
    super(name, columnReference);
  }

  @Override
  public String getDefinition() {
    return "";
  }

  @Override
  public TableConstraintType getType() {
    return TableConstraintType.weak_association;
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
