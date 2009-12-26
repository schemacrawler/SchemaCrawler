package schemacrawler.tools.executable;


import java.sql.Connection;

import javax.sql.DataSource;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.options.OutputOptions;

public class SchemaCrawlerExecutable
  implements Executable
{

  private final Executable executable;

  public SchemaCrawlerExecutable(final SchemaCrawlerCommandLine commandLine)
    throws SchemaCrawlerException
  {
    this(commandLine.getCommand());
    if (commandLine != null)
    {
      final Config config = commandLine.getConfig();
      if (config != null)
      {
        setConfig(config);
      }
      final ConnectionOptions connectionOptions = commandLine
        .getConnectionOptions();
      if (connectionOptions != null)
      {
        setConnectionOptions(connectionOptions);
      }
      final OutputOptions outputOptions = commandLine.getOutputOptions();
      if (outputOptions != null)
      {
        setOutputOptions(outputOptions);
      }
      final SchemaCrawlerOptions schemaCrawlerOptions = commandLine
        .getSchemaCrawlerOptions();
      if (schemaCrawlerOptions != null)
      {
        setSchemaCrawlerOptions(schemaCrawlerOptions);
      }
    }
  }

  public SchemaCrawlerExecutable(final String command)
    throws SchemaCrawlerException
  {
    final String commandExecutableClassName = CommandRegistry
      .lookupCommandExecutableClassName(command);
    try
    {
      final Class<? extends Executable> commandExecutableClass = (Class<? extends Executable>) Class
        .forName(commandExecutableClassName);
      executable = commandExecutableClass.newInstance();
      executable.setCommand(command);
    }
    catch (final ClassNotFoundException e)
    {
      throw new SchemaCrawlerException("Could not instantiate executable", e);
    }
    catch (final InstantiationException e)
    {
      throw new SchemaCrawlerException("Could not instantiate executable", e);
    }
    catch (final IllegalAccessException e)
    {
      throw new SchemaCrawlerException("Could not instantiate executable", e);
    }
  }

  public void execute()
    throws Exception
  {
    executable.execute();
  }

  public void execute(final Connection connection)
    throws ExecutionException
  {
    executable.execute(connection);
  }

  public void execute(final DataSource dataSource)
    throws ExecutionException
  {
    executable.execute(dataSource);
  }

  public String getCommand()
  {
    return executable.getCommand();
  }

  public Config getConfig()
  {
    return executable.getConfig();
  }

  public ConnectionOptions getConnectionOptions()
  {
    return executable.getConnectionOptions();
  }

  public OutputOptions getOutputOptions()
  {
    return executable.getOutputOptions();
  }

  public SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return executable.getSchemaCrawlerOptions();
  }

  public void setCommand(final String command)
  {
    throw new UnsupportedOperationException("Cannot set the command");
  }

  public void setConfig(final Config config)
  {
    executable.setConfig(config);
  }

  public void setConnectionOptions(final ConnectionOptions connectionOptions)
  {
    executable.setConnectionOptions(connectionOptions);
  }

  public void setOutputOptions(final OutputOptions outputOptions)
  {
    executable.setOutputOptions(outputOptions);
  }

  public void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
  }

  public void setSchemaInfoLevel(final SchemaInfoLevel infoLevel)
  {
    executable.setSchemaInfoLevel(infoLevel);
  }

}
