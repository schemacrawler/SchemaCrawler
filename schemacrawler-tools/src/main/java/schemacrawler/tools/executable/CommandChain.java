/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;

/**
 * Allows chaining multiple executables together, that produce different
 * artifacts, such as an image and a HTML file.
 */
public final class CommandChain
  extends BaseCommandChain
{

  private static final String COMMAND = "chain";

  /**
   * Copy configuration settings from another command.
   * 
   * @param scCommand
   *        Other command
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public CommandChain(final SchemaCrawlerCommand scCommand)
    throws SchemaCrawlerException
  {
    super(COMMAND);

    requireNonNull(scCommand, "No command provided, for settings");

    // Copy all configuration
    setSchemaCrawlerOptions(scCommand.getSchemaCrawlerOptions());
    setAdditionalConfiguration(scCommand.getAdditionalConfiguration());
    setOutputOptions(scCommand.getOutputOptions());

    setCatalog(scCommand.getCatalog());
    setConnection(scCommand.getConnection());
    setDatabaseSpecificOptions(scCommand.getDatabaseSpecificOptions());
  }

  public final SchemaCrawlerCommand addNext(final String command,
                                            final OutputFormat outputFormat,
                                            final Path outputFile)
    throws SchemaCrawlerException
  {
    requireNonNull(command, "No command provided");
    requireNonNull(outputFormat, "No output format provided");
    requireNonNull(outputFile, "No output file provided");

    return addNext(command,
                   outputFormat.getFormat(),
                   outputFile.normalize().toAbsolutePath().toString());
  }

  public final SchemaCrawlerCommand addNext(final String command,
                                            final String outputFormat,
                                            final String outputFileName)
    throws SchemaCrawlerException
  {
    requireNonNull(command, "No command provided");
    requireNonNull(outputFormat, "No output format provided");
    requireNonNull(outputFileName, "No output file name provided");

    final Path outputFile = Paths.get(outputFileName);
    final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                          outputFile);

    return addNextAndConfigureForExecution(command, outputOptions);
  }

  @Override
  public void execute()
    throws Exception
  {
    executeChain();
  }

}
