/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import static us.fatehi.utility.Utility.isBlank;

import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

public final class CommandOptions {

  @Option(
      names = {"-c", "--command"},
      required = true,
      description = "SchemaCrawler command",
      completionCandidates = AvailableCommands.class)
  private String command;

  @Spec private Model.CommandSpec spec;

  public String getCommand() {
    if (isBlank(command)) {
      throw new ParameterException(spec.commandLine(), "No command provided");
    }

    return command;
  }
}
