/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static picocli.CommandLine.Help.TextTable.forColumns;
import static picocli.CommandLine.Model.UsageMessageSpec.DEFAULT_USAGE_WIDTH;
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
import picocli.CommandLine.Help.Column.Overflow;
import picocli.CommandLine.Help.TextTable;
import us.fatehi.utility.property.PropertyName;

abstract class BaseAvailableRegistryPlugins implements Iterable<String> {

  private static final Pattern metaCommand = Pattern.compile("<.*>");
  protected final Collection<PropertyName> plugins;

  protected BaseAvailableRegistryPlugins(final Collection<PropertyName> plugins) {
    this.plugins = requireNonNull(plugins, "No plugins provided");
  }

  public final boolean isEmpty() {
    return plugins.isEmpty();
  }

  @Override
  public final Iterator<String> iterator() {
    return stream().collect(Collectors.toList()).iterator();
  }

  public void printHelp(final PrintStream out) {
    if (!isEmpty() && out != null) {
      out.println();
      out.printf("Available %s:%n", getName());
      out.println(commands());
    }
  }

  public int size() {
    return plugins.size();
  }

  public final Stream<String> stream() {
    return plugins.stream()
        .filter(Objects::nonNull)
        .filter(plugin -> !metaCommand.matcher(plugin.getName()).matches())
        .map(PropertyName::getName);
  }

  protected abstract String getName();

  private final TextTable commands() {
    final int indent = 2;
    final int spacing = 2;
    int maxNameLength = 0;
    for (final PropertyName plugin : plugins) {
      final int length = plugin.getName().length();
      if (length > maxNameLength) {
        maxNameLength = length;
      }
    }
    maxNameLength = maxNameLength + spacing;

    final CommandLine.Help.ColorScheme.Builder colorSchemaBuilder =
        new CommandLine.Help.ColorScheme.Builder();
    colorSchemaBuilder.ansi(CommandLine.Help.Ansi.OFF);
    final TextTable textTable =
        forColumns(
            colorSchemaBuilder.build(),
            new Column(maxNameLength, indent, Overflow.SPAN),
            new Column(DEFAULT_USAGE_WIDTH - maxNameLength, indent, Overflow.WRAP));

    for (final PropertyName plugin : plugins) {
      textTable.addRowValues(plugin.getName(), plugin.getDescription());
    }
    return textTable;
  }
}
