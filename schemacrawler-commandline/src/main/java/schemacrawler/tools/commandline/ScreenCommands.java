/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import org.jline.shell.Command;
import org.jline.shell.CommandGroup;
import org.jline.shell.CommandSession;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;

final class ScreenCommands implements CommandGroup {

  private static final class ClearCommand implements Command {

    @Override
    public List<String> aliases() {
      return List.of("cls");
    }

    @Override
    public Object execute(final CommandSession session, final String[] args) throws Exception {
      requireNonNull(session, "No session provided");
      final Terminal terminal = requireNonNull(session.terminal(), "No terminal provided");
      terminal.puts(Capability.clear_screen);
      terminal.flush();
      return 0;
    }

    @Override
    public String name() {
      return "clear";
    }
  }

  @Override
  public Collection<Command> commands() {
    return List.of(new ClearCommand());
  }

  @Override
  public String name() {
    return "screen";
  }
}
