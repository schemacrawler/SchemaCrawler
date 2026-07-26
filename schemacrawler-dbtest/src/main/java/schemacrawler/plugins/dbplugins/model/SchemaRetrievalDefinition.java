/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import java.util.Map;

/** Represents schema metadata retrieval overrides. */
public record SchemaRetrievalDefinition(
    Map<String, String> strategies, Boolean supportsCatalogs, Boolean supportsSchemas) {

  public SchemaRetrievalDefinition() {
    this(null, null, null);
  }

  public SchemaRetrievalDefinition {
    strategies = strategies == null ? Map.of() : Map.copyOf(strategies);
  }
}
