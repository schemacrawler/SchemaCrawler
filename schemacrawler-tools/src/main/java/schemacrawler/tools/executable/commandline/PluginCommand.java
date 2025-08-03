/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.executable.commandline;

import static schemacrawler.tools.executable.commandline.PluginCommandType.command;
import static schemacrawler.tools.executable.commandline.PluginCommandType.loader;
import static schemacrawler.tools.executable.commandline.PluginCommandType.server;
import static schemacrawler.tools.executable.commandline.PluginCommandType.unknown;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.trimToEmpty;
import us.fatehi.utility.Nullable;
import us.fatehi.utility.datasource.DatabaseServerType;
import us.fatehi.utility.property.PropertyName;

public class PluginCommand implements Iterable<PluginCommandOption> {

  public static PluginCommand empty() {
    return new PluginCommand(unknown, "unknown", null, null, null);
  }

  public static PluginCommand newCatalogLoaderCommand(final PropertyName name) {
    return newPluginCommand(loader, name.getName(), "** " + name.getDescription());
  }

  public static PluginCommand newDatabasePluginCommand(final DatabaseServerType dbServerType) {
    return newPluginCommand(
        server,
        dbServerType.getDatabaseSystemIdentifier(),
        "** Connect to " + trimToEmpty(dbServerType.getDatabaseSystemName()));
  }

  public static PluginCommand newPluginCommand(final PropertyName name) {
    return newPluginCommand(command, name.getName(), "** " + name.getDescription());
  }

  public static PluginCommand newPluginCommand(
      final PropertyName name,
      @Nullable final Supplier<String[]> helpDescription,
      @Nullable final Supplier<String[]> helpFooter) {
    return new PluginCommand(
        command, name.getName(), "** " + name.getDescription(), helpDescription, helpFooter);
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
      @Nullable final Supplier<String[]> helpDescription,
      @Nullable final Supplier<String[]> helpFooter) {

    this.type = requireNonNull(type, "No plugin command type provided");
    options = new ArrayList<>();

    this.name = trimToEmpty(name);
    this.helpHeader = trimToEmpty(helpHeader);
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
    if (helpDescription == null) {
      return () -> new String[0];
    }
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
    return helpFooter != null || type != unknown;
  }

  public boolean isEmpty() {
    return type == unknown && options.isEmpty();
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
