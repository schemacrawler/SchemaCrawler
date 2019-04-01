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
import java.util.regex.Pattern;

import picocli.CommandLine;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class GrepOptionsParser
  implements OptionsParser<SchemaCrawlerOptions>
{

  private final CommandLine commandLine;
  private final SchemaCrawlerOptionsBuilder optionsBuilder;

  @CommandLine.Option(names = {
    "--grep-columns" }, description = "grep for tables with column names matching pattern")
  private Pattern grepcolumns;
  @CommandLine.Option(names = {
    "--grep-in-out" }, description = "grep for routines with parameter names matching pattern")
  private Pattern grepinout;
  @CommandLine.Option(names = {
    "--grep-def" }, description = "grep for tables definitions containing pattern")
  private Pattern grepdef;
  @CommandLine.Option(names = {
    "--invert-match" }, description = "Invert the grep match")
  private Boolean invertMatch;
  @CommandLine.Option(names = {
    "--only-matching" }, description = "Show only matching tables, and not foreign keys that reference other non-matching tables")
  private Boolean onlyMatching;

  @CommandLine.Parameters
  private String[] remainder = new String[0];

  public GrepOptionsParser(final SchemaCrawlerOptionsBuilder optionsBuilder)
  {
    commandLine = newCommandLine(this);
    this.optionsBuilder = Objects.requireNonNull(optionsBuilder);
  }

  @Override
  public SchemaCrawlerOptions parse(final String[] args)
  {
    commandLine.parse(args);

    if (grepcolumns != null)
    {
      optionsBuilder.includeGreppedColumns(grepcolumns);
    }
    if (grepinout != null)
    {
      optionsBuilder.includeGreppedRoutineColumns(grepinout);
    }
    if (grepdef != null)
    {
      optionsBuilder.includeGreppedDefinitions(grepdef);
    }

    if (invertMatch != null)
    {
      optionsBuilder.invertGrepMatch(invertMatch);
    }
    if (onlyMatching != null)
    {
      optionsBuilder.grepOnlyMatching(onlyMatching);
    }

    return null;
  }

  @Override
  public String[] getRemainder()
  {
    return remainder;
  }

}
