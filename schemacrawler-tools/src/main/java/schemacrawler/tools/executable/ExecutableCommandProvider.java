package schemacrawler.tools.executable;


import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;

class ExecutableCommandProvider
  implements CommandProvider
{

  private static final Logger LOGGER = Logger
    .getLogger(ExecutableCommandProvider.class.getName());

  private final String command;
  private final String executableClassName;

  ExecutableCommandProvider(final String command,
                            final String executableClassName)
  {
    this.command = command;
    this.executableClassName = executableClassName;
  }

  @Override
  public String getCommand()
  {
    return command;
  }

  @Override
  public String getHelpResource()
  {
    final String helpResource = "/help/"
                                + executableClassName
                                  .substring(executableClassName
                                    .lastIndexOf('.') + 1) + ".txt";
    return helpResource;
  }

  @Override
  public Executable newExecutable()
    throws SchemaCrawlerException
  {

    Class<? extends Executable> commandExecutableClass;
    try
    {
      commandExecutableClass = (Class<? extends Executable>) Class
        .forName(executableClassName);
    }
    catch (final ClassNotFoundException e)
    {
      throw new SchemaCrawlerException("Could not load class "
                                       + executableClassName, e);
    }

    Executable executable;
    try
    {
      executable = commandExecutableClass.newInstance();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.FINE, "Could not instantiate " + executableClassName
                             + " using the default constructor");
      try
      {
        final Constructor<? extends Executable> constructor = commandExecutableClass
          .getConstructor(new Class[] {
            String.class
          });
        executable = constructor.newInstance(command);
      }
      catch (final Exception e1)
      {
        throw new SchemaCrawlerException("Could not instantiate executable for command '"
                                             + command + "'",
                                         e1);
      }
    }

    return executable;
  }

  @Override
  public String toString()
  {
    return executableClassName;
  }

}
