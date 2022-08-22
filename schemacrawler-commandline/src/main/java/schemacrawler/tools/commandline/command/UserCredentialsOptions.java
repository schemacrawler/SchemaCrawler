/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Model;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import us.fatehi.utility.datasource.MultiUseUserCredentials;
import us.fatehi.utility.datasource.UserCredentials;

/** Options for the command-line. */
public final class UserCredentialsOptions {

  @ArgGroup private UserOptions userOptions;
  @ArgGroup private PasswordOptions passwordOptions;
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
