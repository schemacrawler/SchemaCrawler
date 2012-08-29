package schemacrawler.tools.executable;


import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.operation.OperationExecutable;

/**
 * Wrapper executable for any SchemaCrawler command. Looks up the
 * command registry, and instantiates the registered executable for the
 * command. If the command is not a known command,
 * SchemaCrawlerExecutable will check if it is a query configured in the
 * properties. If not, it will assume that a query is specified on the
 * command line, and execute that.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerExecutable
  extends BaseExecutable
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerExecutable.class.getName());

  public SchemaCrawlerExecutable(final String command)
    throws SchemaCrawlerException
  {
    super(command);
  }

  @Override
  protected void executeOn(final Database database, final Connection connection)
    throws Exception
  {
    final Commands commands = new Commands(getCommand());
    if (commands.isEmpty())
    {
      throw new SchemaCrawlerException("No command specified");
    }

    BaseExecutable executable = null;
    final CommandRegistry commandRegistry = new CommandRegistry();

    for (final String command: commands)
    {
      final boolean isCommand = commandRegistry.hasCommand(command);
      final boolean isConfiguredQuery = additionalConfiguration != null
                                        && additionalConfiguration
                                          .containsKey(command);
      // If the command is a direct query
      if (!isCommand && !isConfiguredQuery)
      {
        LOGGER.log(Level.INFO,
                   String.format("Executing as a query, %s", getCommand()));
        executable = new OperationExecutable(getCommand());
        break;
      }
    }

    if (executable == null)
    {
      if (commands.hasMultipleCommands())
      {
        LOGGER.log(Level.INFO, String
          .format("Executing commands [%s] in sequence", commands));
        executable = new CommandDaisyChainExecutable(getCommand());
      }
      else
      {
        executable = (BaseExecutable) commandRegistry
          .newExecutable(getCommand());
        LOGGER.log(Level.INFO, String
          .format("Executing command \"%s\" using executable %s",
                  getCommand(),
                  executable.getClass().getName()));
      }
    }

    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(additionalConfiguration);
    executable.setOutputOptions(outputOptions);

    executable.executeOn(database, connection);
  }

}
