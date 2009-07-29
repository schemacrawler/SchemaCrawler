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


import javax.sql.DataSource;

import schemacrawler.main.HelpOptions;
import schemacrawler.main.SchemaCrawlerCommandLine;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.Commands;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.schematext.SchemaCrawlerExecutable;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextOptions;

/**
 * Basic SchemaCrawler integrations executor.
 * 
 * @author Sualeh Fatehi
 */
public abstract class SchemaCrawlerIntegrationsExecutable
  extends SchemaCrawlerExecutable
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
    final Config config = commandLine.getConfig();
    final SchemaCrawlerOptions schemaCrawlerOptions = commandLine
      .getSchemaCrawlerOptions();
    final OutputOptions outputOptions = commandLine.getOutputOptions();

    final Commands commands = commandLine.getCommands();
    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType
      .valueOf(commands.iterator().next().getName());

    final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(config,
                                                                      outputOptions,
                                                                      schemaTextDetailType);

    setSchemaCrawlerOptions(schemaCrawlerOptions);
    setToolOptions(schemaTextOptions);
    doExecute(commandLine.createDataSource());
  }

  protected abstract void doExecute(final DataSource dataSource)
    throws Exception;

  protected abstract HelpOptions getHelpOptions();

}
