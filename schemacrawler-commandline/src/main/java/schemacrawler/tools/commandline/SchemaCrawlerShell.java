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
package schemacrawler.tools.commandline;


import java.util.logging.Level;

import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import picocli.shell.jline3.PicocliJLineCompleter;
import schemacrawler.tools.commandline.shellcommand.SchemaCrawlerShellCommands;
import schemacrawler.tools.commandline.shellcommand.StateFactory;
import sf.util.SchemaCrawlerLogger;

public class SchemaCrawlerShell
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawlerShell.class.getName());

  public static void main(final String[] args)
    throws Exception
  {
    final StateFactory stateFactory = new StateFactory();
    final SchemaCrawlerShellCommands commands = new SchemaCrawlerShellCommands();
    final CommandLine cmd = new CommandLine(commands, stateFactory);

    final Terminal terminal = TerminalBuilder.builder().build();
    final LineReader reader = LineReaderBuilder.builder().terminal(terminal)
      .completer(new PicocliJLineCompleter(cmd.getCommandSpec()))
      .parser(new DefaultParser()).build();
    commands.setReader(reader);
    final String prompt = "schemacrawler> ";

    while (true)
    {
      try
      {
        final String line = reader
          .readLine(prompt, null, (MaskingCallback) null, null);
        final ParsedLine pl = reader.getParser().parse(line, 0);
        final String[] arguments = pl.words().toArray(new String[0]);
        cmd.parseWithHandlers(new CommandLine.RunLast(),
                              new CommandLine.DefaultExceptionHandler(),
                              arguments);
      }
      catch (final UserInterruptException e)
      {
        // Ignore
      }
      catch (final EndOfFileException e)
      {
        return;
      }
      catch (final Exception e)
      {
        System.err.println(e.getMessage());
        LOGGER.log(Level.WARNING, e.getMessage(), e);
      }
    }
  }

}
