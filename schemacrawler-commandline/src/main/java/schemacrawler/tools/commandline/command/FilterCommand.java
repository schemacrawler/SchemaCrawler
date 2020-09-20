/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import schemacrawler.schemacrawler.FilterOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
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
      names = "--no-empty-tables",
      description = {
        "Includes only tables that have rows of data",
        "Requires table row counts to be loaded",
        "Optional, default is false"
      },
      negatable = true)
  private Boolean noemptytables;

  @Option(
      names = "--parents",
      description = {
        "<parents> is the number of generations of ancestors for the tables "
            + "selected by grep, and shown in the results",
        "Optional, default is 0"
      })
  private Integer parents;

  @Spec private Model.CommandSpec spec;

  public FilterCommand(final SchemaCrawlerShellState state) {
    super(state);
  }

  @Override
  public void run() {

    final SchemaCrawlerOptions schemaCrawlerOptions = state.getSchemaCrawlerOptions();

    final FilterOptionsBuilder optionsBuilder =
        FilterOptionsBuilder.builder().fromOptions(schemaCrawlerOptions.getFilterOptions());

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

    if (noemptytables != null) {
      optionsBuilder.noEmptyTables(noemptytables);
    }

    state.withFilterOptions(optionsBuilder.toOptions());
  }
}
