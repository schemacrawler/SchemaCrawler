/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

/** Event manipulation type. */
public enum EventManipulationType {

  /** Unknown */
  unknown,
  /** Insert */
  insert,
  /** Delete */
  delete,
  /** Update */
  update
}
