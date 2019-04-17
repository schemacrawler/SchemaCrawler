/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.commandline;


import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import java.lang.reflect.Method;
import java.sql.Connection;

import schemacrawler.schemacrawler.*;
import schemacrawler.tools.commandline.command.ConnectCommands;
import schemacrawler.tools.commandline.parser.*;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.SchemaCrawlerLogger;
import us.fatehi.commandlineparser.CommandLineUtility;

/**
 * Utility for parsing the SchemaCrawler command-line.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerCommandLine
  implements CommandLine
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawlerCommandLine.class.getName());

  private final String command;
  private final OutputOptions outputOptions;
  private final SchemaCrawlerOptions schemaCrawlerOptions;

  private final SchemaCrawlerShellState state;

  public SchemaCrawlerCommandLine(final String[] args)
    throws SchemaCrawlerException
  {

    if (args == null)
    {
      throw new SchemaCrawlerRuntimeException(
        "No command-line arguments provided");
    }

    state = new SchemaCrawlerShellState();

    final picocli.CommandLine.IFactory factory = new StateFactory(state);

    newCommandLine(new ConfigParser(state), factory)
      .parseWithHandlers(new picocli.CommandLine.RunLast(),
                         new picocli.CommandLine.DefaultExceptionHandler<>(),
                         args);
    final Method connectMethod = picocli.CommandLine
      .getCommandMethods(ConnectCommands.class, "connect").get(0);
    newCommandLine(connectMethod, factory)
      .parseWithHandlers(new picocli.CommandLine.RunLast(),
                         new picocli.CommandLine.DefaultExceptionHandler<>(),
                         args);

    final CommandParser commandParser = new CommandParser();
    commandParser.parse(args);
    command = commandParser.getCommand();

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().fromConfig(state.getAdditionalConfiguration());
    final FilterOptionsParser filterOptionsParser = new FilterOptionsParser(
      schemaCrawlerOptionsBuilder);
    filterOptionsParser.parse(args);
    final GrepOptionsParser grepOptionsParser = new GrepOptionsParser(
      schemaCrawlerOptionsBuilder);
    grepOptionsParser.parse(args);
    final LimitOptionsParser limitOptionsParser = new LimitOptionsParser(
      schemaCrawlerOptionsBuilder);
    limitOptionsParser.parse(args);
    final InfoLevelParser infoLevelParser = new InfoLevelParser(
      schemaCrawlerOptionsBuilder);
    infoLevelParser.parse(args);
    schemaCrawlerOptions = schemaCrawlerOptionsBuilder.toOptions();

    final OutputOptionsBuilder outputOptionsBuilder = OutputOptionsBuilder
      .builder().fromConfig(state.getAdditionalConfiguration());
    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(
      outputOptionsBuilder);
    outputOptionsParser.parse(args);
    outputOptions = outputOptionsBuilder.toOptions();

    final Config config = state.getAdditionalConfiguration();

    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
      .builder().fromConfig(state.getAdditionalConfiguration());
    final ShowOptionsParser showOptionsParser = new ShowOptionsParser(
      schemaTextOptionsBuilder);
    showOptionsParser.parse(args);
    final SortOptionsParser sortOptionsParser = new SortOptionsParser(
      schemaTextOptionsBuilder);
    sortOptionsParser.parse(args);
    config.putAll(schemaTextOptionsBuilder.toConfig());

    final Config argsMap = CommandLineUtility.parseArgs(args);
    config.putAll(argsMap);

  }

  @Override
  public void execute()
    throws Exception
  {
    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(
      command);
    // Configure
    executable.setOutputOptions(outputOptions);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(state.getAdditionalConfiguration());
    try (final Connection connection = state.getDataSource().getConnection())
    {
      final SchemaRetrievalOptions schemaRetrievalOptions = state
        .getSchemaRetrievalOptionsBuilder().toOptions();

      // Execute the command
      executable.setConnection(connection);
      executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
      executable.execute();
    }
  }

}
