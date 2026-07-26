/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

/** Represents a default JDBC URL property. */
public record UrlPropertyDefinition(String name, String value) {

  public UrlPropertyDefinition() {
    this("property", "");
  }

  public UrlPropertyDefinition {
    name = requireNotBlank(name, "No URL property name provided");
    value = requireNonNull(value, "No URL property value provided");
  }
}
