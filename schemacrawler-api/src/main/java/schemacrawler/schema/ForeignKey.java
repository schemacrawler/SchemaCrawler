/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

/** Represents a foreign-key mapping to a primary key in another table. */
public interface ForeignKey extends TableReference {

  /**
   * Gets the deferrability.
   *
   * @return Deferrability
   */
  ForeignKeyDeferrability getDeferrability();

  /**
   * Gets the delete rule.
   *
   * @return Delete rule
   */
  ForeignKeyUpdateRule getDeleteRule();

  /**
   * Gets the update rule.
   *
   * @return Update rule
   */
  ForeignKeyUpdateRule getUpdateRule();
}
