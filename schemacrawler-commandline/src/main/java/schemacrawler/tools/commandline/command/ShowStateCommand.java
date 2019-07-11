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
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

import java.util.function.Supplier;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import sf.util.ObjectToStringFormat;
import sf.util.SchemaCrawlerLogger;

@Command(name = "showstate",
         header = "** Show State Options - Show internal state",
         description = {
           "",
         })
public final class ShowStateCommand
  implements Runnable
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger.getLogger(
    ShowStateCommand.class.getName());

  private final SchemaCrawlerShellState state;

  @Option(names = { "--show-log", },
          description = {
            "Show log on console; otherwise log to log file",
            "<boolean> can be true or false",
            "Optional, defaults to false"
          },
          negatable = true)
  private Boolean showlog;

  public ShowStateCommand(final SchemaCrawlerShellState state)
  {
    this.state = requireNonNull(state, "No state provided");
  }

  @Override
  public void run()
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = state.getSchemaCrawlerOptionsBuilder()
                                                           .toOptions();
    final SchemaRetrievalOptions schemaRetrievalOptions = state.getSchemaRetrievalOptionsBuilder()
                                                               .toOptions();

    if (!state.isConnected())
    {
      log(Level.CONFIG, () -> "No database connection available");
    }
    log(Level.CONFIG, new ObjectToStringFormat(schemaRetrievalOptions));
    log(Level.CONFIG, new ObjectToStringFormat(schemaCrawlerOptions));

    log(Level.FINE,
        new ObjectToStringFormat(state.getAdditionalConfiguration()));
  }

  private void log(Level level, Supplier<String> toLog)
  {
    if (showlog != null && showlog)
    {
      System.out.println(toLog.get());
    }
    else
    {
      LOGGER.log(level, toLog);
    }
  }

}
