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


import picocli.CommandLine;
import schemacrawler.tools.databaseconnector.UserCredentials;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class ConnectionOptions
{

  @CommandLine.ArgGroup(exclusive = true)
  private DatabaseConnectionOptions databaseConnectionOptions;
  @CommandLine.Spec
  private CommandLine.Model.CommandSpec spec;
  @CommandLine.Mixin
  private UserCredentialsOptions userCredentialsOptions;

  public DatabaseConnectable getDatabaseConnectable()
  {
    if (databaseConnectionOptions == null)
    {
      throw new CommandLine.ParameterException(spec.commandLine(),
                                               "No database connection options provided");
    }

    final DatabaseConnectable databaseConnectable = databaseConnectionOptions.getDatabaseConnectable();
    if (databaseConnectable == null)
    {
      throw new CommandLine.ParameterException(spec.commandLine(),
                                               "No database connection options provided");
    }

    return databaseConnectable;
  }

  UserCredentials getUserCredentials()
  {

    if (userCredentialsOptions == null)
    {
      throw new CommandLine.ParameterException(spec.commandLine(),
                                               "No database connection credentials provided");
    }
    final UserCredentials userCredentials = userCredentialsOptions.getUserCredentials();
    return userCredentials;
  }

}
