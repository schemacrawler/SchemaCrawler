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


import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import picocli.CommandLine;
import schemacrawler.tools.databaseconnector.UserCredentials;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class ConnectionOptionsParser
{

  private final CommandLine commandLine;
  private DatabaseConnectable databaseConnectable;
  @CommandLine.ArgGroup(exclusive = true)
  private DatabaseConnectionOptions databaseConnectionOptions;
  @CommandLine.Parameters
  private String[] remainder;
  @CommandLine.Spec
  private CommandLine.Model.CommandSpec spec;
  @CommandLine.Mixin
  private UserCredentialsParser userCredentialsParser;

  public ConnectionOptionsParser()
  {
    commandLine = newCommandLine(this);
  }

  public DatabaseConnectable getDatabaseConnectable()
  {
    if (databaseConnectionOptions == null)
    {
      throw new CommandLine.ParameterException(spec.commandLine(),
                                               "No database connection options provided");
    }

    databaseConnectable = databaseConnectionOptions.getDatabaseConnectable();
    if (databaseConnectable == null)
    {
      throw new CommandLine.ParameterException(spec.commandLine(),
                                               "No database connection options provided");
    }

    return databaseConnectable;
  }

  public UserCredentials getUserCredentials()
  {

    if (userCredentialsParser == null)
    {
      throw new CommandLine.ParameterException(spec.commandLine(),
                                               "No database connection credentials provided");
    }
    final UserCredentials userCredentials = userCredentialsParser
      .getUserCredentials();
    return userCredentials;
  }

}
