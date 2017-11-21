/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;

/**
 * Allows chaining multiple executables together, that produce different
 * artifacts, such as an image and a HTML file.
 */
public final class CommandChainExecutable
  extends BaseCommandChainExecutable
{

  private static final String COMMAND = "chain";

  public CommandChainExecutable()
    throws SchemaCrawlerException
  {
    super(COMMAND);
  }

  public final Executable addNext(final String command,
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

  public final Executable addNext(final String command,
                                  final String outputFormat,
                                  final String outputFileName)
    throws SchemaCrawlerException
  {
    try
    {
      final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                            Paths
                                                              .get(outputFileName));

      final Executable executable = commandRegistry
        .configureNewExecutable(command, schemaCrawlerOptions, outputOptions);
      if (executable == null)
      {
        return null;
      }

      executable.setAdditionalConfiguration(additionalConfiguration);

      return addNext(executable);
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException(String
        .format("Cannot chain executable, unknown command, %s - %s - %s",
                command,
                outputFormat,
                outputFileName));
    }
  }

  @Override
  public void executeOn(final Catalog catalog, final Connection connection)
    throws Exception
  {
    executeChain(catalog, connection);
  }

}
