/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

public interface TableConstraintColumn extends Column {

  /**
   * Gets the table constraint this column belongs to.
   *
   * @return Table constraint
   */
  TableConstraint getTableConstraint();

  /**
   * Ordinal position of the column, in the table constraint.
   *
   * @return Ordinal position
   */
  int getTableConstraintOrdinalPosition();
}
