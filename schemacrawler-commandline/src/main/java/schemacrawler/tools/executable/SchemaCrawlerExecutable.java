package schemacrawler.tools.executable;


import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;

public class SchemaCrawlerExecutable
  implements Executable
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerExecutable.class.getName());

  private final Executable executable;

  public SchemaCrawlerExecutable(final String command)
    throws Exception
  {
    final String commandExecutableClassName = CommandRegistry
      .lookupCommandExecutableClassName(command);
    final Class<? extends Executable> commandExecutableClass = (Class<? extends Executable>) Class
      .forName(commandExecutableClassName);

    Executable executable;
    try
    {
      executable = commandExecutableClass.newInstance();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.FINE, "Could not instantiate "
                             + commandExecutableClassName
                             + " using the default constructor", e);
      final Constructor constructor = commandExecutableClass
        .getConstructor(new Class[] {
          String.class
        });
      executable = (Executable) constructor.newInstance(new Object[] {
        command
      });
    }

    this.executable = executable;
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

}
