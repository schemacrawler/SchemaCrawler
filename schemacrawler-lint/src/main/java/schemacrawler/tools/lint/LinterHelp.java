/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.lint;

import java.util.function.Supplier;

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

    final LinterRegistry registry = new LinterRegistry();
    for (final String linterId : registry) {
      final Linter linter = registry.newLinter(linterId);
      if (generateMarkdown) {
        printMarkdownLinterHeader(buffer, linter);
      } else {
        printLinterHeader(buffer, linter);
      }
      buffer
          .append(linter.getDescription())
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

  private void printLinterHeader(final StringBuilder buffer, final Linter linter) {
    buffer.append("Linter: ").append(linter.getLinterId()).append(System.lineSeparator());
  }

  private void printMarkdownHeader(final StringBuilder buffer) {
    buffer
        .append(System.lineSeparator())
        .append("## Lint Checks")
        .append(System.lineSeparator())
        .append(System.lineSeparator());
  }

  private void printMarkdownLinterHeader(final StringBuilder buffer, final Linter linter) {
    buffer
        .append("### Linter: *")
        .append(linter.getLinterId())
        .append("*  ")
        .append(System.lineSeparator());
  }
}
