/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

import java.util.List;

/** Represents a table constraint. */
public interface TableConstraint
    extends DependantObject<Table>, DefinedObject, TypedObject<TableConstraintType> {

  /**
   * Gets the list of columns in ordinal order.
   *
   * @return Columns of the table constraint.
   */
  List<TableConstraintColumn> getConstrainedColumns();

  /**
   * Whether the constraint is deferrable.
   *
   * @return Whether the constraint is deferrable
   */
  boolean isDeferrable();

  /**
   * Whether the constraint is initially deferred.
   *
   * @return Whether the constraint is initially deferred
   */
  boolean isInitiallyDeferred();
}
