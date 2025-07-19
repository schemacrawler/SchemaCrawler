/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

/**
 * Represents a database synonym.
 *
 * <p>(Based on an idea from Matt Albrecht)
 */
public interface Synonym extends DatabaseObject {

  /**
   * Gets the referenced object, which may or may not actually exist.
   *
   * @return Referenced object.
   */
  DatabaseObject getReferencedObject();
}
