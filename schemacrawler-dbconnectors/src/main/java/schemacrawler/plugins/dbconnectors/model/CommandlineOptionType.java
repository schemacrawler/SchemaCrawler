/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors.model;

public enum CommandlineOptionType {
  STRING(String.class),
  INTEGER(Integer.class),
  BOOLEAN(Boolean.class);

  private final Class<?> optionClass;

  CommandlineOptionType(final Class<?> optionClass) {
    this.optionClass = optionClass;
  }

  public Class<?> optionClass() {
    return optionClass;
  }

  @Override
  public String toString() {
    return name().toLowerCase();
  }
}
