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
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class LimitOptionsParser
  implements OptionsParser
{

  private final CommandLine commandLine;
  private final SchemaCrawlerOptionsBuilder optionsBuilder;

  @CommandLine.Option(names = { "--schemas" }, description = "Regular expression to match fully qualified names of schemas to include")
  private Pattern schemas;
  @CommandLine.Option(names = { "--tables" }, description = "Regular expression to match fully qualified names of tables to include")
  private Pattern tables;
  @CommandLine.Option(names = { "--exclude-columns" }, description = "Regular expression to match fully qualified names of columns to exclude")
  private Pattern excludecolumns;
  @CommandLine.Option(names = { "--routines" }, description = "Regular expression to match fully qualified names of routines to include")
  private Pattern routines;
  @CommandLine.Option(names = { "--exclude-in-out" }, description = "Regular expression to match fully qualified names of parameters to exclude")
  private Pattern excludeinout;
  @CommandLine.Option(names = { "--synonyms" }, description = "Regular expression to match fully qualified names of synonyms to include")
  private Pattern synonyms;
  @CommandLine.Option(names = { "--sequences" }, description = "Regular expression to match fully qualified names of sequences to include")
  private Pattern sequences;

  @CommandLine.Option(names = { "--table-types" }, description = "Comma-separated list of table types")
  private String tabletypes;
  @CommandLine.Option(names = { "--routine-types" }, description = "Comma-separated list of routine types")
  private String routinetypes;

  @CommandLine.Parameters
  private String[] remainder = new String[0];

  public LimitOptionsParser(final SchemaCrawlerOptionsBuilder optionsBuilder)
  {
    commandLine = newCommandLine(this);
    this.optionsBuilder = Objects.requireNonNull(optionsBuilder);
  }

  @Override
  public void parse(final String[] args)
  {
    commandLine.parse(args);

    if (schemas != null)
    {
      optionsBuilder.includeSchemas(schemas);
    }
    if (tables != null)
    {
      optionsBuilder.includeTables(tables);
    }
    if (excludecolumns != null)
    {
      optionsBuilder
        .includeColumns(new RegularExpressionExclusionRule(excludecolumns));
    }
    if (routines != null)
    {
      optionsBuilder.includeRoutines(routines);
    }
    if (excludeinout != null)
    {
      optionsBuilder
        .includeRoutineColumns(new RegularExpressionExclusionRule(excludeinout));
    }

    if (synonyms != null)
    {
      optionsBuilder.includeSynonyms(synonyms);
    }
    if (sequences != null)
    {
      optionsBuilder.includeSequences(sequences);
    }

    if (tabletypes != null)
    {
      optionsBuilder.tableTypes(tabletypes);
    }
    if (routinetypes != null)
    {
      optionsBuilder.routineTypes(routinetypes);
    }

  }

  @Override
  public String[] getRemainder()
  {
    return remainder;
  }

}
