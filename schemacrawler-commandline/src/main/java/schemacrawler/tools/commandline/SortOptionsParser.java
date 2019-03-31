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
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class SortOptionsParser
  implements OptionsParser<SchemaTextOptions>
{

  private final CommandLine commandLine;
  private final SchemaTextOptionsBuilder optionsBuilder;

  @CommandLine.Option(names = { "--sort-tables" }, description = "Whether to sort tables")
  private Boolean sorttables;
  @CommandLine.Option(names = { "--sort-columns" }, description = "Whether to sort table columns")
  private Boolean sortcolumns;
  @CommandLine.Option(names = { "--sort-routines" }, description = "Whether to sort routines")
  private Boolean sortroutines;
  @CommandLine.Option(names = { "--sort-in-out" }, description = "Whether to routine parameters")
  private Boolean sortinout;

  @CommandLine.Parameters
  private String[] remainder = new String[0];

  public SortOptionsParser(final SchemaTextOptionsBuilder optionsBuilder)
  {
    commandLine = newCommandLine(this);
    this.optionsBuilder = Objects.requireNonNull(optionsBuilder);
  }

  @Override
  public SchemaTextOptions parse(final String[] args)
  {
    commandLine.parse(args);

    if (sorttables != null)
    {
      optionsBuilder.sortTables(sorttables);
    }
    if (sortcolumns != null)
    {
      optionsBuilder.sortTableColumns(sortcolumns);
    }

    if (sortroutines != null)
    {
      optionsBuilder.sortRoutines(sortroutines);
    }
    if (sortinout != null)
    {
      optionsBuilder.sortInOut(sortinout);
    }

    return null;
  }

  @Override
  public String[] getRemainder()
  {
    return remainder;
  }

}
