/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.List;

/** Represents an additional named JDBC option. */
public record AdditionalOptionDefinition(
    String name, String type, String urlxKey, String defaultValue, List<String> help) {

  public AdditionalOptionDefinition() {
    this("option", "String", null, null, List.of());
  }

  public AdditionalOptionDefinition {
    name = requireNotBlank(name, "No additional option name provided");
    type = requireNotBlank(type, "No additional option type provided");
    urlxKey = isBlank(urlxKey) ? null : urlxKey;
    defaultValue = defaultValue == null ? null : defaultValue;
    help = help == null ? List.of() : List.copyOf(help);
    requireNonNull(help, "No additional option help provided");
  }
}
