/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.executable;


import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.DatabaseSpecificOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.operation.OperationExecutable;
import sf.util.ObjectToString;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

import java.sql.Connection;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

/**
 * Wrapper executable for any SchemaCrawler command. Looks up the
 * command registry, and instantiates the registered executable for the
 * command. If the command is not a known command,
 * SchemaCrawlerExecutable will check if it is a query configured in the
 * properties. If not, it will assume that a query is specified on the
 * command-line, and execute that.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerExecutable
    extends BaseExecutable
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawlerExecutable.class.getName());

  public SchemaCrawlerExecutable(final String command)
    throws SchemaCrawlerException
  {
    super(command);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public final void execute(final Connection connection,
                            final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
      throws Exception
  {
    requireNonNull(connection, "No connection provided");
    requireNonNull(databaseSpecificOverrideOptions,
                   "No database specific overrides provided");

    databaseSpecificOptions = new DatabaseSpecificOptions(connection,
                                                          databaseSpecificOverrideOptions);

    LOGGER.log(Level.INFO,
               new StringFormat("Executing SchemaCrawler command <%s>",
                                getCommand()));
    if (LOGGER.isLoggable(Level.CONFIG))
    {
      LOGGER.log(Level.CONFIG,
                 String.format("Executable: %s", this.getClass().getName()));
      LOGGER.log(Level.CONFIG, ObjectToString.toString(schemaCrawlerOptions));
      LOGGER.log(Level.CONFIG, ObjectToString.toString(outputOptions));
      LOGGER.log(Level.CONFIG, databaseSpecificOptions.toString());
    }
    if (LOGGER.isLoggable(Level.FINE))
    {
      LOGGER.log(Level.FINE, ObjectToString.toString(additionalConfiguration));
    }

    final SchemaCrawler schemaCrawler = new SchemaCrawler(connection,
                                                          databaseSpecificOverrideOptions);
    final Catalog catalog = schemaCrawler.crawl(schemaCrawlerOptions);

    executeOn(catalog, connection);
  }

  private void executeOn(final Catalog catalog, final Connection connection)
    throws Exception
  {
    final Commands commands = new Commands(getCommand());
    if (commands.isEmpty())
    {
      throw new SchemaCrawlerException("No command specified");
    }

    BaseStagedExecutable executable = null;
    final CommandRegistry commandRegistry = new CommandRegistry();

    for (final String command: commands)
    {
      final boolean isCommand = commandRegistry
        .supportsCommand(command, schemaCrawlerOptions, outputOptions);
      final boolean isConfiguredQuery = additionalConfiguration != null
                                        && additionalConfiguration
                                          .containsKey(command);
      // If the command is a direct query
      if (!isCommand && !isConfiguredQuery)
      {
        LOGGER.log(Level.INFO,
                   new StringFormat("Executing as a query <%s>", getCommand()));
        executable = new OperationExecutable(getCommand());
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setOutputOptions(outputOptions);
        break;
      }
    }

    if (executable == null)
    {
      if (commands.hasMultipleCommands())
      {
        LOGGER.log(Level.INFO,
                   new StringFormat("Executing commands <%s> in sequence",
                                    commands));
        executable = new CommandDaisyChainExecutable(getCommand());
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setOutputOptions(outputOptions);
      }
      else
      {
        executable = (BaseStagedExecutable) commandRegistry
          .configureNewExecutable(getCommand(),
                                  schemaCrawlerOptions,
                                  outputOptions);
        LOGGER
          .log(Level.INFO,
               new StringFormat("Executing command <%s> using executable <%s>",
                                getCommand(),
                                executable.getClass().getName()));
      }
    }

    executable.setDatabaseSpecificOptions(databaseSpecificOptions);
    executable.setAdditionalConfiguration(additionalConfiguration);

    executable.executeOn(catalog, connection);
  }

}
