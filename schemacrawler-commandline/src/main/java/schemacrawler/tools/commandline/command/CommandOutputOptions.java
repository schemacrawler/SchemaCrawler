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

package schemacrawler.tools.commandline.command;


import static sf.util.Utility.isBlank;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import picocli.CommandLine;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class CommandOutputOptions
{

  @CommandLine.Option(names = {
    "-o", "--output-file" }, description = "Outfile file path and name")
  private File outputFile;
  @CommandLine.Option(names = {
    "--output-format" }, description = "Outfile format")
  private String outputFormatValue;
  @CommandLine.Option(names = {
    "-m", "--title" }, description = "Title for output")
  private String title;

  public Optional<String> getTitle()
  {
    if (isBlank(title))
    {
      return Optional.empty();
    }
    else
    {
      return Optional.of(title);
    }
  }

  public Optional<Path> getOutputFile()
  {
    if (outputFile != null)
    {
      return Optional.of(outputFile.toPath().toAbsolutePath());
    }
    else
    {
      return Optional.empty();
    }
  }

  public Optional<String> getOutputFormatValue()
  {
    if (isBlank(outputFormatValue))
    {
      return Optional.empty();
    }
    else
    {
      return Optional.of(outputFormatValue);
    }
  }

}
