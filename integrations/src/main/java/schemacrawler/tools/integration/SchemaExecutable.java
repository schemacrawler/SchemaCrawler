/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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


import schemacrawler.crawl.Config;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.main.SchemaCrawlerCommandLine;
import schemacrawler.tools.Command;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextOptions;
import sf.util.CommandLineUtility;

/**
 * An executor that uses a template renderer to render a schema.
 * 
 * @author sfatehi
 */
public abstract class SchemaExecutable
  extends Executable<SchemaTextOptions>
{

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @param helpResource
   *        A resource for help text
   * @throws Exception
   *         On an exception
   */
  public void executeOnSchema(final String[] args, final String helpResource)
    throws Exception
  {
    CommandLineUtility.checkForHelp(args, helpResource);
    CommandLineUtility.setLogLevel(args);

    final SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(args);
    final Config config = commandLine.getConfig();
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config,
                                                                               commandLine
                                                                                 .getPartition());
    final OutputOptions outputOptions = commandLine.getOutputOptions();

    final Command[] commands = commandLine.getCommands();
    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType
      .valueOf(commands[0].getName());

    final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(config,
                                                                      outputOptions,
                                                                      schemaTextDetailType);

    setSchemaCrawlerOptions(schemaCrawlerOptions);
    setToolOptions(schemaTextOptions);
    execute(commandLine.createDataSource());
  }

}
