/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;

public class DatabaseConnectionGroupOptions {

  @ArgGroup(exclusive = false, heading = "%nFor connecting to specific databases, use%n")
  private ServerHostConnectionGroupOptions databaseConfigConnectionOptions;

  @ArgGroup(
      exclusive = false,
      heading = "%nIf your database does not have a " + "SchemaCrawler plug-in, use%n")
  private UrlConnectionGroupOptions databaseUrlConnectionOptions;

  DatabaseConnectionOptions getDatabaseConnectionOptions() {
    if (databaseConfigConnectionOptions != null) {
      return databaseConfigConnectionOptions.toDatabaseConnectionOptions();
    } else {
      return databaseUrlConnectionOptions.toDatabaseConnectionOptions();
    }
  }
}
