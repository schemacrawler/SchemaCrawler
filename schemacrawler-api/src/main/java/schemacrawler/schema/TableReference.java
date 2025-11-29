/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import java.util.List;

/** Represents a foreign-key mapping to a primary key in another table. */
public interface TableReference
    extends NamedObject,
        AttributedObject,
        DescribedObject,
        TableConstraint,
        Iterable<ColumnReference> {

  /**
   * Gets the list of column pairs.
   *
   * @return Column pairs
   */
  List<ColumnReference> getColumnReferences();

  /**
   * Gets dependent or child table for this reference.
   *
   * @return Dependent table for this reference.
   */
  default Table getDependentTable() {
    return getForeignKeyTable();
  }

  /**
   * Gets the dependent table with an imported foreign key.
   *
   * @return Dependent table.
   */
  Table getForeignKeyTable();

  /**
   * Gets the referenced table.
   *
   * @return Referenced table.
   */
  Table getPrimaryKeyTable();

  /**
   * Gets referenced or parent table for this reference.
   *
   * @return Referenced table for this reference.
   */
  default Table getReferencedTable() {
    return getPrimaryKeyTable();
  }
}
