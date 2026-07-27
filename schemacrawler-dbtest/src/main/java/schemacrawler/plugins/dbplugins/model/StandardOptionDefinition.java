/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static us.fatehi.utility.Utility.isBlank;

import java.util.List;

/** Represents help and default values for a standard option. */
public record StandardOptionDefinition(String defaultValue, List<String> help) {

  public StandardOptionDefinition() {
    this(null, null);
  }

  public StandardOptionDefinition {
    defaultValue = isBlank(defaultValue) ? null : defaultValue;
    help = help == null ? List.of() : List.copyOf(help);
  }
}
