/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.commandline.command;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Model;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import us.fatehi.utility.datasource.MultiUseUserCredentials;
import us.fatehi.utility.datasource.UserCredentials;

/** Options for the command-line. */
public final class UserCredentialsOptions {

  @ArgGroup(heading = "Specify the database user name using one of these options\n")
  private UserOptions userOptions;

  @ArgGroup(heading = "Specify the database password using one of these options\n")
  private PasswordOptions passwordOptions;

  @Spec private Model.CommandSpec spec;

  public UserCredentials getUserCredentials() {
    return new MultiUseUserCredentials(getUser(), getPassword());
  }

  private String getPassword() {
    if (passwordOptions == null) {
      return null;
    }

    try {
      return passwordOptions.getPassword();
    } catch (final Exception e) {
      throw new ParameterException(spec.commandLine(), "No password provided", e);
    }
  }

  private String getUser() {
    if (userOptions == null) {
      return null;
    }

    try {
      return userOptions.getUser();
    } catch (final Exception e) {
      throw new ParameterException(spec.commandLine(), "No user provided", e);
    }
  }
}
