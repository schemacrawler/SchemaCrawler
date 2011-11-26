/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.executable;


import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;

public abstract class ExecutableChain
{

  private final List<Executable> executables;
  private Database database;
  private Connection connection;

  protected ExecutableChain()
  {
    executables = new ArrayList<Executable>();
  }

  public final Executable addNext(final Executable executable)
  {
    executables.add(executable);
    return executable;
  }

  public final void execute()
    throws Exception
  {
    for (final Executable executable: executables)
    {
      ((BaseExecutable) executable).executeOn(database, connection);
    }
  }

  protected Executable addNext(final String command,
                               final SchemaCrawlerOptions schemaCrawlerOptions,
                               final Config additionalConfiguration,
                               final String outputFormat,
                               final File outputFile)
    throws SchemaCrawlerException
  {
    try
    {
      final CommandRegistry commandRegistry = new CommandRegistry();
      final Executable executable = commandRegistry.newExecutable(command);

      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            outputFile);

      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setAdditionalConfiguration(additionalConfiguration);
      executable.setOutputOptions(outputOptions);

      return addNext(executable);
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException(String.format("Bad command, %s - %s - %s",
                                                     command,
                                                     outputFormat,
                                                     outputFile));
    }
  }

  protected final Connection getConnection()
  {
    return connection;
  }

  protected final Database getDatabase()
  {
    return database;
  }

  protected final void setConnection(final Connection connection)
  {
    this.connection = connection;
  }

  protected final void setDatabase(final Database database)
  {
    this.database = database;
  }

}
