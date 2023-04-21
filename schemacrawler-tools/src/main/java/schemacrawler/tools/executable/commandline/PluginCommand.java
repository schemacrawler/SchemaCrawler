/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.executable.commandline;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.executable.commandline.PluginCommandType.command;
import static schemacrawler.tools.executable.commandline.PluginCommandType.loader;
import static schemacrawler.tools.executable.commandline.PluginCommandType.server;
import static schemacrawler.tools.executable.commandline.PluginCommandType.unknown;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;

public class PluginCommand implements Iterable<PluginCommandOption> {

  public static PluginCommand empty() {
    return new PluginCommand(unknown, null, null, null, null);
  }

  public static PluginCommand newCatalogLoaderCommand(final String name, final String helpHeader) {
    return newPluginCommand(loader, name, helpHeader);
  }

  public static PluginCommand newDatabasePluginCommand(final String name, final String helpHeader) {
    return newPluginCommand(server, name, helpHeader);
  }

  public static PluginCommand newPluginCommand(final String name, final String helpHeader) {
    return newPluginCommand(command, name, helpHeader);
  }

  public static PluginCommand newPluginCommand(
      final String name,
      final String helpHeader,
      final Supplier<String[]> helpDescription,
      final Supplier<String[]> helpFooter) {
    return new PluginCommand(command, name, helpHeader, helpDescription, helpFooter);
  }

  private static PluginCommand newPluginCommand(
      final PluginCommandType pluginType, final String name, final String helpHeader) {
    return new PluginCommand(pluginType, name, helpHeader, null, null);
  }

  private final PluginCommandType type;
  private final Supplier<String[]> helpDescription;
  private final String helpHeader;
  private final String name;
  private final Supplier<String[]> helpFooter;
  private final Collection<PluginCommandOption> options;

  private PluginCommand(
      final PluginCommandType type,
      final String name,
      final String helpHeader,
      final Supplier<String[]> helpDescription,
      final Supplier<String[]> helpFooter) {

    this.type = requireNonNull(type, "No plugin command type provided");
    this.options = new ArrayList<>();

    this.name = name;

    if (isBlank(helpHeader)) {
      this.helpHeader = null;
    } else {
      this.helpHeader = helpHeader;
    }

    this.helpDescription = helpDescription;

    this.helpFooter = helpFooter;
  }

  public PluginCommand addOption(
      final String name, final Class<?> valueClass, final String... helpText) {
    final PluginCommandOption option = new PluginCommandOption(name, valueClass, helpText);
    options.add(option);
    return this;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PluginCommand)) {
      return false;
    }
    final PluginCommand that = (PluginCommand) o;
    return Objects.equals(name, that.name);
  }

  public Supplier<String[]> getHelpDescription() {
    return helpDescription;
  }

  public Supplier<String[]> getHelpFooter() {
    return addStandardFooter(type, helpFooter);
  }

  public String getHelpHeader() {
    return helpHeader;
  }

  public String getName() {
    return type.toPluginCommandName(name);
  }

  public Collection<PluginCommandOption> getOptions() {
    return new ArrayList<>(options);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  public boolean hasHelpDescription() {
    return helpDescription != null;
  }

  public boolean hasHelpFooter() {
    return helpFooter != null || type != PluginCommandType.unknown;
  }

  public boolean isEmpty() {
    return isBlank(name) && options.isEmpty();
  }

  @Override
  public Iterator<PluginCommandOption> iterator() {
    return options.iterator();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", PluginCommand.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("options=" + options)
        .toString();
  }

  private Supplier<String[]> addStandardFooter(
      final PluginCommandType type, final Supplier<String[]> helpFooter) {

    final String[] helpFooterArray = helpFooter == null ? null : helpFooter.get();
    final List<String> newFooter = new ArrayList<>();
    if (helpFooterArray != null && helpFooterArray.length > 0) {
      newFooter.addAll(Arrays.asList(helpFooterArray));
    }
    switch (type) {
      case command:
        newFooter.add("Add command options to the `execute` command in the SchemaCrawler Shell");
        break;
      case loader:
        newFooter.add("Add loader options to the `load` command in the SchemaCrawler Shell");
        break;
      case server:
        newFooter.add("Add connection options to the `connect` command in the SchemaCrawler Shell");
        break;

      default:
        break;
    }

    final String[] newFooterArray = newFooter.toArray(new String[0]);

    return () -> newFooterArray;
  }
}
