/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.executable.commandline;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.StringJoiner;

public class PluginCommandOption {

  private final String[] helpText;
  private final String name;
  private final Class<?> valueClass;

  PluginCommandOption(final String name, final Class<?> valueClass, final String... helpText) {
    this.name = requireNonNull(name, "No option name provided");

    if (helpText == null) {
      this.helpText = new String[0];
    } else {
      this.helpText = helpText;
    }

    if (valueClass == null) {
      this.valueClass = String.class;
    } else {
      this.valueClass = valueClass;
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PluginCommandOption)) {
      return false;
    }
    final PluginCommandOption that = (PluginCommandOption) o;
    return Objects.equals(getName(), that.getName());
  }

  public String[] getHelpText() {
    return helpText;
  }

  public String getName() {
    return name;
  }

  public Class<?> getValueClass() {
    return valueClass;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", PluginCommandOption.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("valueClass=" + valueClass.getCanonicalName())
        .toString();
  }
}
