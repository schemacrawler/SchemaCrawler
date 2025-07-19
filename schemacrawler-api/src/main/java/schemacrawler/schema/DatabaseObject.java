/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import schemacrawler.schemacrawler.Identifiers;

/** Represents a database object. */
public interface DatabaseObject extends NamedObject, AttributedObject, DescribedObject {

  Schema getSchema();

  /**
   * Allows a strategy for quoting identifiers to be considered.
   *
   * @param identifiers Identifier quoting strategy.
   */
  void withQuoting(Identifiers identifiers);
}
