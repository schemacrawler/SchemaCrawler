/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import picocli.CommandLine.Command;
import picocli.CommandLine.ExecutionException;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;
import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.commandline.shell.AvailableCommandsCommand;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.utility.OutputOptionsConfig;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.diagram.DiagramOutputFormat;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

@Command(
    name = "execute",
    header = "** Execute a SchemaCrawler command",
    description = {""},
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"execute"},
    optionListHeading = "Options:%n")
public class ExecuteCommand extends BaseStateHolder implements Runnable {

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(AvailableCommandsCommand.class.getName());

  @Mixin private CommandOptions commandOptions;
  @Mixin private CommandOutputOptions commandOutputOptions;
  @Spec private Model.CommandSpec spec;

  public ExecuteCommand(final ShellState state) {
    super(state);
  }

  @Override
  public void run() {

    if (!state.isLoaded()) {
      throw new ExecutionException(spec.commandLine(), "No database metadata is loaded");
    }

    Connection connection = null;
    if (state.isConnected()) {
      connection = state.getDataSource().get();
    }

    try {

      final ParseResult parseResult = spec.commandLine().getParseResult();
      final Map<String, Object> commandConfig = retrievePluginOptions(parseResult);
      state.addConfig(commandConfig);

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
      final SchemaRetrievalOptions schemaRetrievalOptions = state.getSchemaRetrievalOptions();
      final OutputOptions outputOptions = outputOptionsBuilder.toOptions();
      final Config additionalConfiguration = state.getConfig();

      // Output file name has to be specified for diagrams
      // (Check after output options have been built)
      if (DiagramOutputFormat.isSupportedFormat(outputOptions.getOutputFormatValue())
          && !commandOutputOptions.getOutputFile().isPresent()) {
        throw new SchemaCrawlerRuntimeException(
            "Output file has to be specified for schema diagrams");
      }

      final Catalog catalog = state.getCatalog();
      final String command = commandOptions.getCommand();

      LOGGER.log(Level.INFO, new StringFormat("Executing SchemaCrawler command <%s>", command));
      LOGGER.log(Level.INFO, new ObjectToStringFormat(outputOptions));

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.setAdditionalConfiguration(additionalConfiguration);
      executable.setSchemaRetrievalOptions(schemaRetrievalOptions);

      executable.setConnection(connection);
      executable.setCatalog(catalog);

      executable.execute();
    } catch (final Exception e) {
      throw new ExecutionException(spec.commandLine(), "Cannot execute SchemaCrawler command", e);
    }
  }

  /**
   * SchemaCrawler plugins are registered on-the-fly, by adding them to the classpath. Inspect the
   * command-line to see if there are any additional plugin-specific options passed in from the
   * command-line, and put them in the configuration.
   *
   * @param parseResult Result of parsing the command-line
   * @return Config with additional plugin-specific command-line options
   * @throws SchemaCrawlerException On an exception
   */
  private Map<String, Object> retrievePluginOptions(final ParseResult parseResult)
      throws SchemaCrawlerException {
    requireNonNull(parseResult, "No parse result provided");

    final Map<String, Object> commandConfig = new HashMap<>();

    final List<OptionSpec> matchedOptionSpecs = parseResult.matchedOptions();
    for (final OptionSpec matchedOptionSpec : matchedOptionSpecs) {
      if (matchedOptionSpec.userObject() != null) {
        continue;
      }
      final Object optionValue = matchedOptionSpec.getValue();
      if (optionValue == null) {
        continue;
      }
      final String optionName = matchedOptionSpec.longestName().replaceFirst("^\\-{0,2}", "");
      commandConfig.put(optionName, optionValue);
    }

    return commandConfig;
  }
}
