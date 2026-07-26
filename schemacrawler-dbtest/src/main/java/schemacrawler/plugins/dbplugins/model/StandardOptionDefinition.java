/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import java.util.List;

/** Represents help and default values for a standard option. */
public record StandardOptionDefinition(List<String> help, String defaultValue) {

  public StandardOptionDefinition() {
    this(null, null);
  }

  public StandardOptionDefinition {
    help = help == null ? List.of() : List.copyOf(help);
  }
}
