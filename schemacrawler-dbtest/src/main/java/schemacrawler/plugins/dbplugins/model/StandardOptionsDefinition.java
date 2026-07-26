/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

/** Represents standard plugin options metadata. */
public record StandardOptionsDefinition(
    StandardOptionDefinition server,
    StandardOptionDefinition host,
    StandardOptionDefinition port,
    StandardOptionDefinition database) {

  public StandardOptionsDefinition() {
    this(null, null, null, null);
  }

  public StandardOptionsDefinition {
    server = server == null ? new StandardOptionDefinition() : server;
    host = host == null ? new StandardOptionDefinition() : host;
    port = port == null ? new StandardOptionDefinition() : port;
    database = database == null ? new StandardOptionDefinition() : database;
  }
}
