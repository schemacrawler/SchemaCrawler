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

import static schemacrawler.tools.commandline.utility.CommandLineUtility.matchedOptionValues;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import picocli.CommandLine.Command;
import picocli.CommandLine.ExecutionException;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.commandline.shell.AvailableCommandsCommand;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.utility.OutputOptionsConfig;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

@Command(
    name = "execute",
    header = "** Execute a SchemaCrawler command",
    description = {""},
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"execute"},
    optionListHeading = "Options:%n",
    footer = {
      "",
      "For additional options, specific to individual SchemaCrawler commands,",
      "run SchemaCrawler with: `-h commands`",
      "or from the SchemaCrawler interactive shell: `help commands`"
    })
public class ExecuteCommand extends BaseStateHolder implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(AvailableCommandsCommand.class.getName());

  @Mixin private CommandOptions commandOptions;
  @Mixin private CommandOutputOptions commandOutputOptions;
  @Spec private Model.CommandSpec spec;

  public ExecuteCommand(final ShellState state) {
    super(state);
  }

  @Override
  public void run() {

    try {
      // Parse and save command options
      saveCommandOptions();

      final SchemaCrawlerExecutable executable = configureExecutable();

      if (!state.isLoaded() && !state.isDeferCatalogLoad()) {
        throw new ExecutionException(spec.commandLine(), "Database metadata is not loaded");
      }
      if (!state.isConnected()) {
        throw new ExecutionException(spec.commandLine(), "Not able to make database connection");
      }

      final SchemaRetrievalOptions schemaRetrievalOptions = state.getSchemaRetrievalOptions();
      final DatabaseConnectionSource dataSource = state.getDataSource();
      final Catalog catalog = state.getCatalog();

      executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
      executable.setDataSource(dataSource);
      executable.setCatalog(catalog);

      executable.execute();

    } catch (final Exception e) {
      throw new ExecutionException(spec.commandLine(), "Cannot execute SchemaCrawler command", e);
    }
  }

  private SchemaCrawlerExecutable configureExecutable() {

    final OutputOptionsBuilder outputOptionsBuilder =
        OutputOptionsConfig.fromConfig(null, state.getConfig());

    if (commandOutputOptions.getOutputFile().isPresent()) {
      outputOptionsBuilder.withOutputFile(commandOutputOptions.getOutputFile().get());
    } else {
      outputOptionsBuilder.withConsoleOutput();
    }
    commandOutputOptions
        .getOutputFormatValue()
        .ifPresent(outputOptionsBuilder::withOutputFormatValue);
    commandOutputOptions.getTitle().ifPresent(outputOptionsBuilder::title);

    final SchemaCrawlerOptions schemaCrawlerOptions = state.getSchemaCrawlerOptions();
    final OutputOptions outputOptions = outputOptionsBuilder.toOptions();
    final Config additionalConfig = state.getConfig();

    // Output file name has to be specified for diagrams
    // (Check after output options have been built)
    if (DiagramOutputFormat.isSupportedFormat(outputOptions.getOutputFormatValue())
        && !commandOutputOptions.getOutputFile().isPresent()) {
      throw new ConfigurationException("Output file has to be specified for schema diagrams");
    }

    final String command = commandOptions.getCommand();

    LOGGER.log(Level.INFO, new StringFormat("Setting up SchemaCrawler command <%s>", command));
    LOGGER.log(Level.CONFIG, new ObjectToStringFormat(outputOptions));

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable.setAdditionalConfiguration(additionalConfig);
    return executable;
  }

  private void saveCommandOptions() {
    final ParseResult parseResult = spec.commandLine().getParseResult();
    final Map<String, Object> commandConfig = matchedOptionValues(parseResult);
    LOGGER.log(Level.INFO, "Loaded command config");
    LOGGER.log(Level.CONFIG, new ObjectToStringFormat(commandConfig));
    state.setCommandOptions(commandConfig);
  }
}
