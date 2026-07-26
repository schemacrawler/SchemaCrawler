/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import java.util.List;

/** Represents help text for the standard plugin options. */
public record HelpDefinition(
    List<String> server, List<String> host, List<String> port, List<String> database) {

  public HelpDefinition() {
    this(null, null, null, null);
  }

  public HelpDefinition {
    server = server == null ? List.of() : List.copyOf(server);
    host = host == null ? List.of() : List.copyOf(host);
    port = port == null ? List.of() : List.copyOf(port);
    database = database == null ? List.of() : List.copyOf(database);
  }
}
