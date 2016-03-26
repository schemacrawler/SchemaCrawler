/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;
import sf.util.StringFormat;

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
  public Executable configureNewExecutable(final SchemaCrawlerOptions schemaCrawlerOptions,
                                           final OutputOptions outputOptions)
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
      LOGGER.log(Level.FINE,
                 new StringFormat("Could not instantiate using the default constructor, %s",
                                  executableClassName));
      try
      {
        final Constructor<? extends Executable> constructor = commandExecutableClass
          .getConstructor(new Class[] { String.class });
        executable = constructor.newInstance(command);
      }
      catch (final Exception e1)
      {
        throw new SchemaCrawlerException("Could not instantiate executable for command '"
                                         + command + "'", e1);
      }
    }

    if (executable != null)
    {
      if (schemaCrawlerOptions != null)
      {
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      }
      if (outputOptions != null)
      {
        executable.setOutputOptions(outputOptions);
      }
    }

    return executable;
  }

  @Override
  public String getCommand()
  {
    return command;
  }

  @Override
  public String getHelpResource()
  {
    final String helpResource = "/help/DefaultExecutable.txt";
    return helpResource;
  }

  @Override
  public String toString()
  {
    return executableClassName;
  }

}
