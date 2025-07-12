/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import schemacrawler.tools.commandline.command.ConnectCommand;

@Command(name = "database-connection-test")
public class ConnectionTestCommands {
  @Mixin private ConnectCommand connect;
}
