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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
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
@CommandLine.Command(name = "limit",
                     header = "** Limit Options - Limit database object metadata",
                     description = {
                       "",
                     })
public final class LimitCommand
  implements Runnable
{

  private final SchemaCrawlerShellState state;

  @CommandLine.Option(names = { "--exclude-columns" },
                      description = {
                        "<excludecolumns> is a regular expression to match fully qualified column names, "
                        + "in the form \"CATALOGNAME.SCHEMANAME.TABLENAME.COLUMNNAME\" - "
                        + "for example, --exclude-columns=.*\\.STREET|.*\\.PRICE matches columns named "
                        + "STREET or PRICE in any table",
                        "Columns that match the pattern are not displayed",
                        "Optional, default is to show all columns"
                      })
  private Pattern excludecolumns;
  @CommandLine.Option(names = { "--exclude-in-out" },
                      description = {
                        "<excludeinout> is a regular expression to match fully qualified parameter names "
                        + "- for example, --exclude-in-out=@p1|@p2 matches parameters named @p1 or @p2 "
                        + "in any procedure",
                        "Parameters that match the pattern are not displayed",
                        "Optional, default is to show all parameters"
                      })
  private Pattern excludeinout;
  @CommandLine.Option(names = { "--routines" },
                      description = {
                        "<routines> is a regular expression to match fully qualified stored procedure "
                        + "or function names, in the form \"CATALOGNAME.SCHEMANAME.ROUTINENAME\" "
                        + "- for example, --routines=.*\\.C.*|.*\\.P.* matches any routines "
                        + "whose names start with C or P",
                        "Routines that do not match the pattern are not displayed",
                        "Use --routines= to omit routines",
                        "Optional, defaults to showing no routines"
                      })
  private Pattern routines;
  @CommandLine.Option(names = { "--routine-types" },
                      split = ",",
                      description = {
                        "<routinetypes> is a comma-separated list of routine types "
                        + "of PROCEDURE,FUNCTION",
                        "Optional, defaults to PROCEDURE,FUNCTION"
                      })
  private String[] routinetypes;
  @CommandLine.Option(names = { "--schemas" },
                      description = {
                        "<schemas> is a regular expression to match fully qualified schema names, "
                        + "in the form \"CATALOGNAME.SCHEMANAME\" - for example, "
                        + "--schemas=.*\\.C.*|.*\\.P.* matches any schemas whose names start "
                        + "with C or P",
                        "Schemas that do not match the pattern are not displayed",
                        "Optional, defaults to showing all schemas"
                      })
  private Pattern schemas;
  @CommandLine.Option(names = { "--sequences" },
                      description = {
                        "<sequences> is a regular expression to match fully qualified "
                        + "sequence names, in the form \"CATALOGNAME.SCHEMANAME.SEQUENCENAME\" "
                        + "- for example, --sequences=.*\\.C.*|.*\\.P.* matches any sequences "
                        + "whose names start with C or P",
                        "Sequences that do not match the pattern are not displayed",
                        "Use --sequences= to omit sequences",
                        "Sequences will only be shown when -infolevel=maximum",
                        "Optional, defaults to showing no sequences"
                      })
  private Pattern sequences;
  @CommandLine.Option(names = { "--synonyms" },
                      description = {
                        "<synonyms> is a regular expression to match fully "
                        + "qualified synonym names, in the form \"CATALOGNAME.SCHEMANAME.SYNONYMNAME\" "
                        + "- for example, --synonyms=.*\\.C.*|.*\\.P.* matches any synonyms "
                        + "whose names start with C or P",
                        "Synonyms that do not match the pattern are not displayed",
                        "Synonyms will only be shown when -infolevel=maximum",
                        "Use --synonyms= to omit synonyms",
                        "Optional, defaults to showing no synonyms"
                      })
  private Pattern synonyms;
  @CommandLine.Option(names = { "--tables" },
                      description = {
                        "<regular-expression> is a regular expression to match fully qualified "
                        + "table names, in the form \"CATALOGNAME.SCHEMANAME.TABLENAME\" "
                        + "- for example, --tables=.*\\.C.*|.*\\.P.* matches any tables "
                        + "whose names start with C or P",
                        "Tables that do not match the pattern are not displayed",
                        "Use with care, since --tables= actually takes tables out of consideration "
                        + "from the perspective of SchemaCrawler - to filter tables, look into the "
                        + "grep options",
                        "Optional, defaults to showing all tables"
                      })
  private Pattern tables;
  @CommandLine.Option(names = { "--table-types" },
                      split = ",",
                      description = {
                        "<tabletypes> is a comma-separated list of table types supported by the database",
                        "If no value is specified, all types of tables are shown",
                        "Optional, defaults to TABLE,VIEW"
                      })
  private String[] tabletypes;

  public LimitCommand(final SchemaCrawlerShellState state)
  {
    this.state = requireNonNull(state, "No state provided");
  }

  @Override
  public void run()
  {
    final SchemaCrawlerOptionsBuilder optionsBuilder = state.getSchemaCrawlerOptionsBuilder();

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
      optionsBuilder.includeColumns(new RegularExpressionExclusionRule(
        excludecolumns));
    }
    if (routines != null)
    {
      optionsBuilder.includeRoutines(routines);
    }
    if (excludeinout != null)
    {
      optionsBuilder.includeRoutineColumns(new RegularExpressionExclusionRule(
        excludeinout));
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
      final RoutineType routineType = enumValue(routineTypeString.toLowerCase(
        Locale.ENGLISH), RoutineType.unknown);
      routineTypesCollection.add(routineType);
    }
    return routineTypesCollection;
  }

}
