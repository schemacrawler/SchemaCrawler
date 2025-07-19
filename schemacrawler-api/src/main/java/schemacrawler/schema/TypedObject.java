/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

public interface TypedObject<T extends Comparable<? super T>> {

  /**
   * Gets the type of the object. Synonym for another getter method.
   *
   * @return Type of the object
   */
  T getType();
}
