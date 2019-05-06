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
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
@CommandLine.Command(name = "show",
                     header = "** Show Options - Show information in output",
                     description = {
                       "",
                     })
public final class ShowCommand
  implements Runnable
{

  private final SchemaCrawlerShellState state;

  @CommandLine.Option(names = { "--no-info" },
                      description = {
                        "Hide database information",
                        "--no-info=<boolean>",
                        "<boolean> can be true or false",
                        "Optional, defaults to false"
                      })
  private Boolean noinfo;
  @CommandLine.Option(names = { "--no-remarks" },
                      description = {
                        "Hide table and column remarks",
                        "--no-remarks=<boolean>",
                        "<boolean> can be true or false",
                        "Optional, defaults to false"
                      })
  private Boolean noremarks;
  @CommandLine.Option(names = { "--portable-names" },
                      description = {
                        "Allow for easy comparison between databases, "
                        + "by hiding foreign key names, constraint names, "
                        + "trigger names, specific names for routines, "
                        + "or index and primary key names, "
                        + "and not showing the fully-qualified table name",
                        "--portable-names=<boolean>",
                        "<boolean> can be true or false",
                        "Optional, defaults to false"
                      })
  private Boolean portablenames;
  @CommandLine.Option(names = { "--weak-associations" },
                      description = {
                        "Show inferred relationships between tables, "
                        + "based on common table and column naming conventions",
                        "--weak-associations=<boolean>",
                        "<boolean> can be true or false",
                        "Optional, defaults to false"
                      })
  private Boolean weakassociations;

  public ShowCommand(final SchemaCrawlerShellState state)
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

    if (noinfo != null)
    {
      optionsBuilder.noInfo(noinfo);
    }
    if (noremarks != null)
    {
      optionsBuilder.noRemarks(noremarks);
    }
    if (weakassociations != null)
    {
      optionsBuilder.weakAssociations(weakassociations);
    }
    if (portablenames != null)
    {
      optionsBuilder.portableNames(portablenames);
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
    state.setAdditionalConfiguration(config);

  }

}
