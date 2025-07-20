/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

@FunctionalInterface
public interface ContainedObject<P> {

  /**
   * Gets the parent.
   *
   * @return Parent
   */
  P getParent();
}
