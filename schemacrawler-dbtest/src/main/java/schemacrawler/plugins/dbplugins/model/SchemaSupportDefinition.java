/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

/** Represents explicit schema/catalog support overrides. */
public record SchemaSupportDefinition(Boolean supportsCatalogs, Boolean supportsSchemas) {

  public SchemaSupportDefinition() {
    this(null, null);
  }
}
