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

import picocli.CommandLine;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
@CommandLine.Command(name = "filter",
                     header = {
                       "----- Filter Options ----------------------------------------------------------",
                       "Filter database object metadata",
                     },
                     description = {
                       "",
                     })
public final class FilterCommand
  implements Runnable
{

  private final SchemaCrawlerShellState state;

  @CommandLine.Option(names = {
    "--children"
  },
                      description = "Number of generations of descendents for the tables selected by grep")
  private Integer children;
  @CommandLine.Option(names = {
    "--no-empty-tables"
  },
                      description = "Include only tables that have rows of data")
  private Boolean noemptytables;
  @CommandLine.Option(names = {
    "--parents"
  },
                      description = "Number of generations of ancestors for the tables selected by grep")
  private Integer parents;

  @CommandLine.Spec
  private CommandLine.Model.CommandSpec spec;

  public FilterCommand(final SchemaCrawlerShellState state)
  {
    this.state = requireNonNull(state, "No state provided");
  }

  @Override
  public void run()
  {

    final SchemaCrawlerOptionsBuilder optionsBuilder = state.getSchemaCrawlerOptionsBuilder();

    if (parents != null)
    {
      if (parents >= 0)
      {
        optionsBuilder.parentTableFilterDepth(parents);
      }
      else
      {
        throw new CommandLine.ParameterException(spec.commandLine(),
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
        throw new CommandLine.ParameterException(spec.commandLine(),
                                                 "Please provide a valid value for --children");
      }
    }

    if (noemptytables != null)
    {
      optionsBuilder.noEmptyTables(noemptytables);
    }

  }

}
