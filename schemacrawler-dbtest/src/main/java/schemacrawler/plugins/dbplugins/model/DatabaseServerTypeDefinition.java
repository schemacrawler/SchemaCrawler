/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static us.fatehi.utility.Utility.requireNotBlank;

import us.fatehi.utility.datasource.DatabaseServerType;

/** Represents database server type information in YAML. */
public record DatabaseServerTypeDefinition(String server, String name) {

  public DatabaseServerTypeDefinition() {
    this("unknown", "Unknown");
  }

  public DatabaseServerTypeDefinition {
    server = requireNotBlank(server, "No database plugin server provided");
    name = requireNotBlank(name, "No database plugin name provided");
  }

  public DatabaseServerType toDatabaseServerType() {
    return new DatabaseServerType(server, name);
  }
}
