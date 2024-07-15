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

package schemacrawler.tools.commandline.command;

import static picocli.CommandLine.Help.Column.Overflow.SPAN;
import static picocli.CommandLine.Help.Column.Overflow.WRAP;
import static picocli.CommandLine.Help.TextTable.forColumns;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.Objects.requireNonNull;
import picocli.CommandLine;
import picocli.CommandLine.Help.Column;
import picocli.CommandLine.Help.TextTable;
import schemacrawler.tools.executable.CommandDescription;

abstract class BaseAvailableCommandDescriptions implements Iterable<String> {

  private static final Pattern metaCommand = Pattern.compile("<.*>");
  protected final Collection<CommandDescription> commandDescriptions;

  protected BaseAvailableCommandDescriptions(
      final Collection<CommandDescription> commandDescriptions) {
    this.commandDescriptions =
        requireNonNull(commandDescriptions, "No command descriptions provided");
  }

  public final boolean isEmpty() {
    return commandDescriptions.isEmpty();
  }

  @Override
  public final Iterator<String> iterator() {
    return stream().collect(Collectors.toList()).iterator();
  }

  public void print(final PrintStream out) {
    if (!isEmpty() && out != null) {
      out.println();
      out.printf("Available %s:%n", getName());
      out.println(commands());
    }
  }

  public int size() {
    return commandDescriptions.size();
  }

  public final Stream<String> stream() {
    return commandDescriptions.stream()
        .filter(Objects::nonNull)
        .filter(commandDescription -> !metaCommand.matcher(commandDescription.getName()).matches())
        .map(CommandDescription::getName);
  }

  protected abstract String getName();

  private final TextTable commands() {
    final CommandLine.Help.ColorScheme.Builder colorSchemaBuilder =
        new CommandLine.Help.ColorScheme.Builder();
    colorSchemaBuilder.ansi(CommandLine.Help.Ansi.OFF);
    final TextTable textTable =
        forColumns(colorSchemaBuilder.build(), new Column(15, 1, SPAN), new Column(65, 1, WRAP));

    for (final CommandDescription commandDescription : commandDescriptions) {
      textTable.addRowValues(commandDescription.getName(), commandDescription.getDescription());
    }
    return textTable;
  }
}
