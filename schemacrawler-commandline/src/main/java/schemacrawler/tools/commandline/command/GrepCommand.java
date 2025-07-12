/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.commandline.command;

import java.util.regex.Pattern;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;

@Command(
    name = "grep",
    header = "** Grep for database object metadata",
    description = {
      "",
    },
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"grep"},
    optionListHeading = "Options:%n")
public final class GrepCommand extends BaseStateHolder implements Runnable {

  @Option(
      names = "--grep-tables",
      description = {
        "<greptables> is a regular expression to match fully qualified table names, "
            + "in the form \"CATALOGNAME.SCHEMANAME.TABLENAME\" "
            + "- for example, --grep-tables=.*\\.COUPONS|.*\\.BOOKS "
            + "matches tables named COUPONS or BOOKS",
        "Optional, default is no grep"
      })
  private Pattern greptables;

  @Option(
      names = "--grep-columns",
      description = {
        "<grepcolumns> is a regular expression to match fully qualified column names, "
            + "in the form \"CATALOGNAME.SCHEMANAME.TABLENAME.COLUMNNAME\" "
            + "- for example, --grep-columns=.*\\.STREET|.*\\.PRICE "
            + "matches columns named STREET or PRICE in any table",
        "Optional, default is no grep"
      })
  private Pattern grepcolumns;

  @Option(
      names = "--grep-def",
      description = {
        "<grepdef> is a regular expression to match text within remarks and definitions "
            + "of views, stored proedures and triggers, if available",
        "Optional, default is no grep"
      })
  private Pattern grepdef;

  @Option(
      names = "--grep-parameters",
      description = {
        "<grepparameters> is a regular expression to match fully qualified routine parameter names, "
            + "in the form \"CATALOGNAME.SCHEMANAME.ROUTINENAME.INOUTNAME\" "
            + "- for example, --grep-parameters=.*\\.STREET|.*\\.PRICE "
            + "matches routine parameters named STREET or PRICE in any routine",
        "Optional, default is no grep"
      })
  private Pattern grepparameters;

  @Option(
      names = "--invert-match",
      description = {
        "Inverts the sense of matching, and shows non-matching tables and columns",
        "Optional, default is false"
      },
      negatable = true)
  private Boolean invertMatch;

  public GrepCommand(final ShellState state) {
    super(state);
  }

  @Override
  public void run() {
    final SchemaCrawlerOptions schemaCrawlerOptions = state.getSchemaCrawlerOptions();

    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().fromOptions(schemaCrawlerOptions.getGrepOptions());

    if (greptables != null) {
      grepOptionsBuilder.includeGreppedTables(greptables);
    }
    if (grepcolumns != null) {
      grepOptionsBuilder.includeGreppedColumns(grepcolumns);
    }
    if (grepparameters != null) {
      grepOptionsBuilder.includeGreppedRoutineParameters(grepparameters);
    }
    if (grepdef != null) {
      grepOptionsBuilder.includeGreppedDefinitions(grepdef);
    }

    if (invertMatch != null) {
      grepOptionsBuilder.invertGrepMatch(invertMatch);
    }

    // Set grep options on the state
    state.withGrepOptions(grepOptionsBuilder.toOptions());
  }
}
