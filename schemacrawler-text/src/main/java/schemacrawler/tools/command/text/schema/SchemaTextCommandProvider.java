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

package schemacrawler.tools.command.text.schema;

import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import schemacrawler.tools.command.text.schema.options.CommandProviderUtility;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

public final class SchemaTextCommandProvider extends BaseCommandProvider {

  private static final String DESCRIPTION_HEADER =
      "Generate text output to show details of a schema";

  public SchemaTextCommandProvider() {
    super(CommandProviderUtility.schemaTextCommands());
  }

  @Override
  public PluginCommand getCommandLineCommand() {

    final PluginCommand pluginCommand =
        newPluginCommand(
            "schema",
            "** " + DESCRIPTION_HEADER,
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
            "portable-names",
            Boolean.class,
            "Allow for easy comparison between databases, "
                + "by hiding or showing foreign key names, constraint names, "
                + "trigger names, specific names for routines, "
                + "or index and primary key names, "
                + "and fully-qualified table names",
            "--portable-names=<boolean>",
            "<boolean> can be true or false",
            "Optional, defaults to false")
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
    final SchemaTextOptions schemaTextOptions =
        SchemaTextOptionsBuilder.builder().fromConfig(config).toOptions();

    final SchemaTextRenderer scCommand = new SchemaTextRenderer(command);
    scCommand.setCommandOptions(schemaTextOptions);
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return supportsOutputFormat(command, outputOptions, TextOutputFormat::isSupportedFormat);
  }
}
