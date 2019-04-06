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


import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import java.util.Objects;

import picocli.CommandLine;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class FilterOptionsParser
  implements OptionsParser
{

  private final CommandLine commandLine;
  private final SchemaCrawlerOptionsBuilder optionsBuilder;

  @CommandLine.Option(names = {
    "--parents" }, description = "Number of generations of ancestors for the tables selected by grep")
  private Integer parents;
  @CommandLine.Option(names = {
    "--children" }, description = "Number of generations of descendents for the tables selected by grep")
  private Integer children;
  @CommandLine.Option(names = {
    "--no-empty-tables" }, description = "Include only tables that have rows of data")
  private Boolean noemptytables;

  @CommandLine.Unmatched
  private String[] remainder = new String[0];

  public FilterOptionsParser(final SchemaCrawlerOptionsBuilder optionsBuilder)
  {
    commandLine = newCommandLine(this);
    this.optionsBuilder = Objects.requireNonNull(optionsBuilder);
  }

  @Override
  public void parse(final String[] args)
  {
    commandLine.parse(args);

    if (parents != null)
    {
      if (parents >= 0)
      {
        optionsBuilder.parentTableFilterDepth(parents);
      }
      else
      {
        throw new SchemaCrawlerCommandLineException(
          "Please provide a valid value for --parents");
      }
    }

    if (children != null)
    {
      if (children >= 0)
      {
        optionsBuilder.childTableFilterDepth(children);
      }
      else
      {
        throw new SchemaCrawlerCommandLineException(
          "Please provide a valid value for --children");
      }
    }

    if (noemptytables != null)
    {
      optionsBuilder.noEmptyTables(noemptytables);
    }

  }

  @Override
  public String[] getRemainder()
  {
    return remainder;
  }

}
