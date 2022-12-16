/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.utility.EnumUtility.enumValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;

@Command(
    name = "limit",
    header = "** Limit database object metadata",
    description = {
      "",
      "When you limit database object metadata, it reduces SchemaCrawler's visibility into other database objects.",
      "From SchemaCrawler's perspective, the other database objects do not exist.",
      ""
    },
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"limit"},
    optionListHeading = "Options:%n")
public final class LimitCommand extends BaseStateHolder implements Runnable {

  @Option(
      names = {"--exclude-columns"},
      description = {
        "<excludecolumns> is a regular expression to match fully qualified column names, "
            + "in the form \"CATALOGNAME.SCHEMANAME.TABLENAME.COLUMNNAME\" - "
            + "for example, --exclude-columns=.*\\.STREET|.*\\.PRICE matches columns named "
            + "STREET or PRICE in any table",
        "Columns that match the pattern are not displayed",
        "Optional, default is to show all columns"
      })
  private Pattern excludecolumns;

  @Option(
      names = {"--exclude-parameters"},
      description = {
        "<excludeparameters> is a regular expression to match fully qualified parameter names "
            + "- for example, --exclude-parameters=@p1|@p2 matches parameters named @p1 or @p2 "
            + "in any procedure",
        "Parameters that match the pattern are not displayed",
        "Optional, default is to show all parameters"
      })
  private Pattern excludeparameters;

  @Option(
      names = {"--routines"},
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

  @Option(
      names = {"--routine-types"},
      split = ",",
      description = {
        "<routinetypes> is a comma-separated list of routine types " + "of PROCEDURE,FUNCTION",
        "Optional, defaults to PROCEDURE,FUNCTION"
      })
  private String[] routinetypes;

  @Option(
      names = {"--schemas"},
      description = {
        "<schemas> is a regular expression to match fully qualified schema names, "
            + "in the form \"CATALOGNAME.SCHEMANAME\" - for example, "
            + "--schemas=.*\\.C.*|.*\\.P.* matches any schemas whose names start "
            + "with C or P",
        "Schemas that do not match the pattern are not displayed",
        "Optional, defaults to showing all schemas"
      })
  private Pattern schemas;

  @Option(
      names = {"--sequences"},
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

  @Option(
      names = {"--synonyms"},
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

  @Option(
      names = {"--tables"},
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

  @Option(
      names = {"--table-types"},
      split = ",",
      description = {
        "<tabletypes> is a comma-separated list of table types supported by the database",
        "If no value is specified, all types of tables are shown",
        "Optional, defaults to TABLE,VIEW"
      })
  private String[] tabletypes;

  public LimitCommand(final ShellState state) {
    super(state);
  }

  @Override
  public void run() {
    final SchemaCrawlerOptions schemaCrawlerOptions = state.getSchemaCrawlerOptions();

    final LimitOptionsBuilder optionsBuilder =
        LimitOptionsBuilder.builder().fromOptions(schemaCrawlerOptions.getLimitOptions());

    if (schemas != null) {
      optionsBuilder.includeSchemas(schemas);
    }
    if (tables != null) {
      optionsBuilder.includeTables(tables);
    }
    if (excludecolumns != null) {
      optionsBuilder.includeColumns(new RegularExpressionExclusionRule(excludecolumns));
    }
    if (routines != null) {
      optionsBuilder.includeRoutines(routines);
    }
    if (excludeparameters != null) {
      optionsBuilder.includeRoutineParameters(
          new RegularExpressionExclusionRule(excludeparameters));
    }

    if (synonyms != null) {
      optionsBuilder.includeSynonyms(synonyms);
    }
    if (sequences != null) {
      optionsBuilder.includeSequences(sequences);
    }

    if (tabletypes != null) {
      optionsBuilder.tableTypes(tabletypes);
    }
    if (routinetypes != null) {
      optionsBuilder.routineTypes(routineTypes());
    }

    state.withLimitOptions(optionsBuilder.toOptions());
  }

  /** Sets routine types from a comma-separated list of routine types. */
  private Collection<RoutineType> routineTypes() {
    if (routinetypes == null) {
      return null;
    }
    final Collection<RoutineType> routineTypesCollection = new HashSet<>();
    for (final String routineTypeString : routinetypes) {
      final RoutineType routineType =
          enumValue(routineTypeString.toLowerCase(Locale.ENGLISH), RoutineType.unknown);
      routineTypesCollection.add(routineType);
    }
    return routineTypesCollection;
  }
}
