/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import java.util.Collection;
import java.util.Optional;

/** Represents a view in the database. */
public interface View extends Table {

  /**
   * Type of WITH CHECK OPTION. Is CASCADE if the original view was created by using the WITH CHECK
   * OPTION. Otherwise, NONE is returned.
   *
   * @return Check option.
   */
  CheckOptionType getCheckOption();

  /**
   * Gets tables used by the view.
   *
   * @return Tables used by the view
   */
  Collection<Table> getTableUsage();

  /**
   * Specifies whether the view is updatable.
   *
   * @return Whether the view is updatable.
   */
  boolean isUpdatable();

  /**
   * Gets a referenced table by unqualified name.
   *
   * @param name Name
   * @return Referenced table.
   */
  <T extends Table> Optional<T> lookupTable(Schema schema, String name);
}
