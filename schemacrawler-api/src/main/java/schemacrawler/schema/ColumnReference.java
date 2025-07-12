/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

import java.io.Serializable;

/** Represents a single column mapping from a primary key column to a foreign key column. */
public interface ColumnReference extends Serializable, Comparable<ColumnReference> {

  /**
   * Gets the foreign key column.
   *
   * @return Foreign key column
   */
  Column getForeignKeyColumn();

  /**
   * Gets the sequence in the foreign key.
   *
   * @return Foreign key sequence
   */
  int getKeySequence();

  /**
   * Gets the primary key column.
   *
   * @return Primary key column
   */
  Column getPrimaryKeyColumn();
}
