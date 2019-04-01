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

package schemacrawler.tools.commandline;


import static sf.util.Utility.isBlank;
import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import java.io.File;
import java.nio.file.Path;

import picocli.CommandLine;
import schemacrawler.tools.options.OutputOptionsBuilder;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class OutputOptionsParser
  implements OptionsParser
{

  private final CommandLine commandLine;

  private final OutputOptionsBuilder outputOptionsBuilder;

  @CommandLine.Option(names = {
    "-o", "--output-file" }, description = "Outfile file path and name")
  private File outputFile;
  @CommandLine.Option(names = {
    "--output-format" }, description = "Outfile format")
  private String outputFormatValue;
  @CommandLine.Option(names = {
    "-m", "--title" }, description = "Title for output")
  private String title;

  @CommandLine.Parameters
  private String[] remainder = new String[0];

  public OutputOptionsParser(final OutputOptionsBuilder outputOptionsBuilder)
  {
    commandLine = newCommandLine(this);
    this.outputOptionsBuilder = outputOptionsBuilder;
  }

  @Override
  public void parse(final String[] args)
  {
    commandLine.parse(args);

    if (title != null)
    {
      outputOptionsBuilder.title(title);
    }

    if (outputFile != null)
    {
      final Path outputFilePath = outputFile.toPath().toAbsolutePath();
      outputOptionsBuilder.withOutputFile(outputFilePath);
    }

    if (!isBlank(outputFormatValue))
    {
      outputOptionsBuilder.withOutputFormatValue(outputFormatValue);
    }
  }

  @Override
  public String[] getRemainder()
  {
    return remainder;
  }

}
