/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.integration;


import java.util.Iterator;

import schemacrawler.main.HelpOptions;
import schemacrawler.main.SchemaCrawlerCommandLine;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.Command;
import schemacrawler.tools.Commands;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextOptions;

/**
 * Basic SchemaCrawler integrations executor.
 * 
 * @author Sualeh Fatehi
 */
public abstract class IntegrationsExecutable
  extends Executable<SchemaTextOptions>
{

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws Exception
   *         On an exception
   */
  public final void main(final String[] args)
    throws Exception
  {
    final SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(args,
                                                                              getHelpOptions());

    schemaCrawlerOptions = commandLine.getSchemaCrawlerOptions();

    final OutputOptions outputOptions = commandLine.getOutputOptions();
    final Commands commands = commandLine.getCommands();
    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType
      .valueOf(getCommand(commands));
    final Config config = commandLine.getConfig();
    toolOptions = new SchemaTextOptions(config,
                                        outputOptions,
                                        schemaTextDetailType);

    execute(commandLine.createDataSource());
  }

  protected abstract HelpOptions getHelpOptions();

  /**
   * Expect one command (further, of type schema text.
   */
  private String getCommand(final Commands commands)
  {
    final Iterator<Command> iterator = commands.iterator();
    if (!iterator.hasNext())
    {
      throw new IllegalArgumentException("No commands specified");
    }
    return iterator.next().getName();
  }

}
