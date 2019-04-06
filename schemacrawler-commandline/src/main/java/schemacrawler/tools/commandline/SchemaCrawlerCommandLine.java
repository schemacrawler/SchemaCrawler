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


import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import schemacrawler.schemacrawler.*;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.UserCredentials;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.iosource.ClasspathInputResource;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import schemacrawler.utility.PropertiesUtility;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

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
  private final Config config;
  private final SchemaCrawlerOptions schemaCrawlerOptions;
  private final OutputOptions outputOptions;
  private final DatabaseConnectionSource databaseConnectionSource;
  private final DatabaseConnector databaseConnector;

  public SchemaCrawlerCommandLine(final String[] args)
    throws SchemaCrawlerException
  {

    if (args == null)
    {
      throw new SchemaCrawlerRuntimeException(
        "No command-line arguments provided");
    }

    // Match the database connector in the best possible way, using the
    // server argument, or the JDBC connection URL
    final ConnectionOptionsParser connectionOptionsParser = new ConnectionOptionsParser();
    connectionOptionsParser.parse(args);
    final DatabaseConnectable databaseConnectable = connectionOptionsParser
      .getDatabaseConnectable();
    databaseConnector = databaseConnectable.getDatabaseConnector();
    LOGGER.log(Level.INFO,
               new StringFormat("Using database plugin <%s>",
                                databaseConnector.getDatabaseServerType()));

    config = loadConfig(args, databaseConnector);

    final CommandParser commandParser = new CommandParser();
    commandParser.parse(args);
    command = commandParser.getCommand();

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().fromConfig(config);
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
      .builder().fromConfig(config);
    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(
      outputOptionsBuilder);
    outputOptionsParser.parse(args);
    outputOptions = outputOptionsBuilder.toOptions();

    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
      .builder().fromConfig(config);
    final ShowOptionsParser showOptionsParser = new ShowOptionsParser(
      schemaTextOptionsBuilder);
    showOptionsParser.parse(args);
    final SortOptionsParser sortOptionsParser = new SortOptionsParser(
      schemaTextOptionsBuilder);
    sortOptionsParser.parse(args);
    config.putAll(schemaTextOptionsBuilder.toConfig());

    final UserCredentialsParser userCredentialsParser = new UserCredentialsParser();
    userCredentialsParser.parse(args);
    final UserCredentials userCredentials = userCredentialsParser
      .getUserCredentials();

    config.putAll(databaseConnector.getConfig());

    // Connect using connection options provided from the command-line,
    // provided configuration, and bundled configuration
    databaseConnectionSource = databaseConnector
      .newDatabaseConnectionSource(databaseConnectable);
    databaseConnectionSource.setUserCredentials(userCredentials);
  }

  @Override
  public void execute()
    throws Exception
  {
    if (databaseConnectionSource == null)
    {
      throw new SchemaCrawlerException("No connection options provided");
    }

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(
      command);
    // Configure
    executable.setOutputOptions(outputOptions);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);
    try (final Connection connection = databaseConnectionSource.get())
    {
      // Get partially built database specific options, built from the
      // classpath resources, and then override from config loaded in
      // from the command-line
      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = databaseConnector
        .getSchemaRetrievalOptionsBuilder(connection);
      schemaRetrievalOptionsBuilder.fromConfig(config);

      final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder
        .toOptions();

      // Execute the command
      executable.setConnection(connection);
      executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
      executable.execute();
    }
  }

  public final String getCommand()
  {
    return command;
  }

  public final Config getConfig()
  {
    return config;
  }

  public final OutputOptions getOutputOptions()
  {
    return outputOptions;
  }

  public final SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  /**
   * Loads configuration from a number of sources, in order of priority.
   *
   * @param dbConnector Database connector
   * @return Loaded configuration
   * @throws SchemaCrawlerException On an exception
   */
  private Config loadConfig(final String[] args,
                            final DatabaseConnector dbConnector)
    throws SchemaCrawlerException
  {
    final Config config = new Config();

    // 1. Get bundled database config
    if (dbConnector != null)
    {
      config.putAll(dbConnector.getConfig());
    }

    // 2. Load config from CLASSPATH, in place
    try
    {
      config.putAll(PropertiesUtility.loadConfig(new ClasspathInputResource(
        "/schemacrawler.config.properties")));
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.CONFIG,
                 "schemacrawler.config.properties not found on CLASSPATH");
    }

    // 3. Load config from files, in place
    final ConfigParser configParser = new ConfigParser();
    configParser.parse(args);
    final Config configFileConfig = configParser.getConfig();
    config.putAll(configFileConfig);

    return config;
  }

  private String[] remainingArgs(final Config config)
  {
    final List<String> remainingArgs = new ArrayList<>();
    final Set<Map.Entry<String, String>> entries = config.entrySet();
    for (final Map.Entry<String, String> entry : entries)
    {
      remainingArgs.add(entry.getKey());
      final String value = entry.getValue();
      if (value != null)
      {
        remainingArgs.add(value);
      }
    }
    return remainingArgs.toArray(new String[0]);
  }

}
