/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.executable.commandline;

import static us.fatehi.utility.Utility.isBlank;

public enum PluginCommandType {
  unknown,
  loader,
  command,
  server;

  public String toPluginCommandName(final String command) {
    if (isBlank(command)) {
      return "";
    }
    return toString() + ":" + command;
  }
}
