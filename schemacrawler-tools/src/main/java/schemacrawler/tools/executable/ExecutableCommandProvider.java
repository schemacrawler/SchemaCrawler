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
package schemacrawler.tools.executable;


import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public abstract class ExecutableCommandProvider
  implements CommandProvider
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ExecutableCommandProvider.class.getName());

  private final Collection<String> supportedCommands;
  private final String executableClassName;

  public ExecutableCommandProvider(final Collection<String> supportedCommands,
                                   final String executableClassName)
  {
    this.supportedCommands = supportedCommands;
    this.executableClassName = executableClassName;
  }

  @Override
  public SchemaCrawlerCommand newSchemaCrawlerCommand(final String command)
    throws SchemaCrawlerException
  {

    Class<? extends SchemaCrawlerCommand> commandExecutableClass;
    try
    {
      commandExecutableClass = (Class<? extends SchemaCrawlerCommand>) Class
        .forName(executableClassName);
    }
    catch (final ClassNotFoundException e)
    {
      throw new SchemaCrawlerException("Could not load class "
                                       + executableClassName,
                                       e);
    }

    SchemaCrawlerCommand scCommand;
    try
    {
      scCommand = commandExecutableClass.newInstance();
    }
    catch (final Exception e)
    {
      LOGGER
        .log(Level.FINE,
             new StringFormat("Could not instantiate using default constructor for class <%s>",
                              executableClassName));
      try
      {
        final Constructor<? extends SchemaCrawlerCommand> constructor = commandExecutableClass
          .getConstructor(String.class);
        scCommand = constructor.newInstance(command);
      }
      catch (final Exception e1)
      {
        throw new SchemaCrawlerException("Could not instantiate executable for command '"
                                         + command + "'",
                                         e1);
      }
    }

    return scCommand;
  }

  @Override
  public boolean supportsSchemaCrawlerCommand(final String command,
                                              final SchemaCrawlerOptions schemaCrawlerOptions,
                                              final OutputOptions outputOptions)
  {
    return supportedCommands.contains(command);
  }

  @Override
  public String toString()
  {
    return executableClassName;
  }

}
