/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model;

/** Categorizes a directed dependency in the schema graph. */
public enum EdgeType {
  FOREIGN_KEY,
  VIEW_DEPENDENCY,
  ROUTINE_DEPENDENCY,
  IMPLICIT_ASSOCIATION,
  SYNONYM_RESOLUTION
}
