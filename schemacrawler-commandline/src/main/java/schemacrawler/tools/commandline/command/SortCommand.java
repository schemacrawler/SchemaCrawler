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

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
@Command(name = "sort",
         header = "** Sort Options - Sort database objects in output",
         description = {
           ""
         })
public final class SortCommand
  implements Runnable
{

  private final SchemaCrawlerShellState state;

  @Option(names = { "--sort-columns" },
          description = {
            "Sort columns in a table alphabetically",
            "--sort-columns=<sortcolumns>",
            "<sortcolumns> can be true or false",
            "Optional, defaults to false"
          },
          negatable = true)
  private Boolean sortcolumns;
  @Option(names = { "--sort-in-out" },
          description = {
            "Sort parameters in a routine alphabetically",
            "--sort-in-out=<sortinout>",
            "<sortinout> can be true or false",
            "Optional, defaults to false"
          },
          negatable = true)
  private Boolean sortinout;
  @Option(names = { "--sort-routines" },
          description = {
            "Sort routines alphabetically",
            "--sort-routines=<sortroutines>",
            "<sortroutines> can be true or false",
            "Optional, defaults to true"
          },
          negatable = true)
  private Boolean sortroutines;
  @Option(names = { "--sort-tables" },
          description = {
            "Sort tables alphabetically",
            "--sort-tables=<sorttables>",
            "<sorttables> can be true or false",
            "Optional, defaults to true"
          },
          negatable = true)
  private Boolean sorttables;

  public SortCommand(final SchemaCrawlerShellState state)
  {
    this.state = requireNonNull(state, "No state provided");
  }

  @Override
  public void run()
  {
    final SchemaTextOptionsBuilder optionsBuilder = SchemaTextOptionsBuilder.builder()
                                                                            .fromConfig(
                                                                              state
                                                                                .getAdditionalConfiguration());

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

    // Set updated configuration options
    final Config config;
    if (state.getAdditionalConfiguration() != null)
    {
      config = state.getAdditionalConfiguration();
    }
    else
    {
      config = new Config();
    }
    config.putAll(optionsBuilder.toConfig());
    state.addAdditionalConfiguration(config);

  }

}
