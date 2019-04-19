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


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.enumValue;

import java.util.*;
import java.util.regex.Pattern;

import picocli.CommandLine;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
@CommandLine.Command(name = "limit", description = "Limit database object metadata")
public final class LimitCommand
  implements Runnable
{

  private final SchemaCrawlerShellState state;

  @CommandLine.Option(names = { "--exclude-columns" }, description = "Regular expression to match fully qualified names of columns to exclude")
  private Pattern excludecolumns;
  @CommandLine.Option(names = { "--exclude-in-out" }, description = "Regular expression to match fully qualified names of parameters to exclude")
  private Pattern excludeinout;
  @CommandLine.Option(names = { "--routines" }, description = "Regular expression to match fully qualified names of routines to include")
  private Pattern routines;
  @CommandLine.Option(names = { "--routine-types" }, split = ",", description = "Comma-separated list of routine types")
  private String[] routinetypes;
  @CommandLine.Option(names = { "--schemas" }, description = "Regular expression to match fully qualified names of schemas to include")
  private Pattern schemas;
  @CommandLine.Option(names = { "--sequences" }, description = "Regular expression to match fully qualified names of sequences to include")
  private Pattern sequences;
  @CommandLine.Option(names = { "--synonyms" }, description = "Regular expression to match fully qualified names of synonyms to include")
  private Pattern synonyms;
  @CommandLine.Option(names = { "--tables" }, description = "Regular expression to match fully qualified names of tables to include")
  private Pattern tables;
  @CommandLine.Option(names = { "--table-types" }, split = ",", description = "Comma-separated list of table types")
  private String[] tabletypes;

  public LimitCommand(final SchemaCrawlerShellState state)
  {
    this.state = requireNonNull(state, "No state provided");
  }

  public void run()
  {
    final SchemaCrawlerOptionsBuilder optionsBuilder = state
      .getSchemaCrawlerOptionsBuilder();

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
      optionsBuilder.tableTypes(Arrays.asList(tabletypes));
    }
    if (routinetypes != null)
    {
      optionsBuilder.routineTypes(routineTypes());
    }

  }

  /**
   * Sets routine types from a comma-separated list of routine types.
   */
  private Collection<RoutineType> routineTypes()
  {
    if (routinetypes == null)
    {
      return null;
    }
    final Collection<RoutineType> routineTypesCollection = new HashSet<>();
    for (final String routineTypeString : routinetypes)
    {
      final RoutineType routineType = enumValue(routineTypeString
                                                  .toLowerCase(Locale.ENGLISH),
                                                RoutineType.unknown);
      routineTypesCollection.add(routineType);
    }
    return routineTypesCollection;
  }

}
