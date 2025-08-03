/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import picocli.CommandLine.Command;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateUtility;

@Command(
    name = "showstate",
    header = "** Show internal state",
    description = {
      "",
    },
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"showstate"},
    optionListHeading = "Options:%n")
public final class ShowStateCommand extends BaseStateHolder implements Runnable {

  public ShowStateCommand(final ShellState state) {
    super(state);
  }

  @Override
  public void run() {
    StateUtility.logState(state, false);
  }
}
