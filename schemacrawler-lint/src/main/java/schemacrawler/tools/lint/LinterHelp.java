/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.lint;

import java.util.Collection;
import java.util.function.Supplier;
import us.fatehi.utility.property.PropertyName;

public final class LinterHelp implements Supplier<String[]> {

  private final boolean generateMarkdown;

  public LinterHelp() {
    this(false);
  }

  public LinterHelp(final boolean generateMarkdown) {
    this.generateMarkdown = generateMarkdown;
  }

  @Override
  public String[] get() {
    final StringBuilder buffer = new StringBuilder(1024);

    if (generateMarkdown) {
      printMarkdownHeader(buffer);
    } else {
      printHelpHeader(buffer);
    }

    final LinterRegistry registry = LinterRegistry.getLinterRegistry();
    final Collection<PropertyName> registeredPlugins = registry.getRegisteredPlugins();
    for (final PropertyName linterName : registeredPlugins) {
      final String linterId = linterName.getName();
      if (generateMarkdown) {
        printMarkdownLinterHeader(buffer, linterId);
      } else {
        printLinterHeader(buffer, linterId);
      }
      buffer
          .append(linterName.getDescription())
          .append(System.lineSeparator())
          .append(System.lineSeparator())
          .append(System.lineSeparator());
    }

    return new String[] {buffer.toString()};
  }

  private void printHelpHeader(final StringBuilder buffer) {
    buffer
        .append(System.lineSeparator())
        .append("Available SchemaCrawler linters:")
        .append(System.lineSeparator())
        .append(System.lineSeparator());
  }

  private void printLinterHeader(final StringBuilder buffer, final String linterId) {
    buffer.append("Linter: ").append(linterId).append(System.lineSeparator());
  }

  private void printMarkdownHeader(final StringBuilder buffer) {
    buffer
        .append(System.lineSeparator())
        .append("## Lint Checks")
        .append(System.lineSeparator())
        .append(System.lineSeparator());
  }

  private void printMarkdownLinterHeader(final StringBuilder buffer, final String linterId) {
    buffer.append("### Linter: *").append(linterId).append("*  ").append(System.lineSeparator());
  }
}
