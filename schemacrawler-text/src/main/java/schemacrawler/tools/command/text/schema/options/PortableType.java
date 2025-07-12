/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.schema.options;

public enum PortableType {
  none("Show fully-qualified names for database objects"),
  names(
      "Do not show fully-qualified names, "
          + "so that output can be diff-ed with "
          + "other databases of the same type"),
  broad(
      "Do not show fully-qualified names, "
          + "and hide trigger action statements, "
          + "so that output can be diff-ed with "
          + "different types of databases"),
  ;

  private final String description;

  private PortableType(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return String.format("[%s] %s", name(), description);
  }
}
