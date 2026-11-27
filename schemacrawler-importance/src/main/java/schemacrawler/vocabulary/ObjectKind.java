/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

/** The kinds of catalog objects whose names are used to build a vocabulary. */
enum ObjectKind {
  SCHEMA,
  TABLE,
  COLUMN,
  TRIGGER,
  ROUTINE,
  PARAMETER,
  CONSTRAINT,
  INDEX,
  SEQUENCE,
  SYNONYM
}
