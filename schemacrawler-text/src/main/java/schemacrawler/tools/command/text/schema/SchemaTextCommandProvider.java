/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.schema;

import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;
import schemacrawler.tools.command.text.schema.options.CommandProviderUtility;
import schemacrawler.tools.command.text.schema.options.PortableType;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.property.PropertyName;

public final class SchemaTextCommandProvider extends BaseCommandProvider {

  public SchemaTextCommandProvider() {
    super(CommandProviderUtility.schemaTextCommands());
  }

  @Override
  public PluginCommand getCommandLineCommand() {

    final PluginCommand pluginCommand =
        newPluginCommand(
            SchemaTextDetailType.schema.toPropertyName(),
            () -> new String[] {"Applies to all commands that show schema information"},
            () -> new String[0]);

    pluginCommand
        // Show options
        .addOption(
            "no-info",
            Boolean.class,
            "Hide or show SchemaCrawler header and database information",
            "--no-info=<boolean>",
            "<boolean> can be true or false",
            "Optional, defaults to false")
        .addOption(
            "no-remarks",
            Boolean.class,
            "Hide or show table and column remarks",
            "--no-remarks=<boolean>",
            "<boolean> can be true or false",
            "Optional, defaults to false")
        .addOption(
            "portable",
            PortableType.class,
            "Allow for easy comparison between databases, "
                + "by hiding or showing foreign key names, constraint names, "
                + "trigger names, specific names for routines, "
                + "or index and primary key names, "
                + "and fully-qualified table names",
            "<portable> is one of ${COMPLETION-CANDIDATES}",
            "Optional, defaults to none")
        // Sort options
        .addOption(
            "sort-columns",
            Boolean.class,
            "Sort columns in a table alphabetically",
            "--sort-columns=<sortcolumns>",
            "<sortcolumns> can be true or false",
            "Optional, defaults to false")
        .addOption(
            "sort-tables",
            Boolean.class,
            "Sort tables alphabetically",
            "--sort-tables=<sorttables>",
            "<sorttables> can be true or false",
            "Optional, defaults to true")
        .addOption(
            "sort-routines",
            Boolean.class,
            "Sort routines alphabetically",
            "--sort-routines=<sortroutines>",
            "<sortroutines> can be true or false",
            "Optional, defaults to true");

    return pluginCommand;
  }

  @Override
  public SchemaTextRenderer newSchemaCrawlerCommand(final String command, final Config config) {
    final PropertyName commandName = lookupSupportedCommand(command);
    if (commandName == null) {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }

    final SchemaTextOptions schemaTextOptions =
        SchemaTextOptionsBuilder.builder().fromConfig(config).toOptions();

    final SchemaTextRenderer scCommand = new SchemaTextRenderer(commandName);
    scCommand.configure(schemaTextOptions);
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return supportsOutputFormat(command, outputOptions, TextOutputFormat::isSupportedFormat);
  }
}
