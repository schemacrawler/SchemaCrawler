/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import schemacrawler.schemacrawler.FilterOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;

@Command(
    name = "filter",
    header = "** Filter database object metadata",
    description = {
      "",
    },
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"filter"},
    optionListHeading = "Options:%n")
public final class FilterCommand extends BaseStateHolder implements Runnable {

  @Option(
      names = "--children",
      description = {
        "<children> is the number of generations of descendants for the tables "
            + "selected by grep, and shown in the results",
        "Optional, default is 0"
      })
  private Integer children;

  @Option(
      names = "--parents",
      description = {
        "<parents> is the number of generations of ancestors for the tables "
            + "selected by grep, and shown in the results",
        "Optional, default is 0"
      })
  private Integer parents;

  @Spec private Model.CommandSpec spec;

  public FilterCommand(final ShellState state) {
    super(state);
  }

  @Override
  public void run() {

    final SchemaCrawlerOptions schemaCrawlerOptions = state.getSchemaCrawlerOptions();

    final FilterOptionsBuilder optionsBuilder =
        FilterOptionsBuilder.builder().fromOptions(schemaCrawlerOptions.filterOptions());

    if (parents != null) {
      if (parents >= 0) {
        optionsBuilder.parentTableFilterDepth(parents);
      } else {
        throw new ParameterException(
            spec.commandLine(), "Please provide a valid value for --parents");
      }
    }

    if (children != null) {
      if (children >= 0) {
        optionsBuilder.childTableFilterDepth(children);
      } else {
        throw new ParameterException(
            spec.commandLine(), "Please provide a valid value for --children");
      }
    }

    state.withFilterOptions(optionsBuilder.toOptions());
  }
}
