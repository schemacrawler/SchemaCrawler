/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

public interface DefinedObject {

  /**
   * Gets the definition.
   *
   * @return Definition
   */
  String getDefinition();

  /**
   * Checks whether there is a definition.
   *
   * @return True if there is a definition
   */
  boolean hasDefinition();
}
