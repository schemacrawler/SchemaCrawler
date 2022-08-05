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

package us.fatehi.utility.datasource;

import java.util.Map;

public class DatabaseConnectionSources {

  public static DatabaseConnectionSource newDatabaseConnectionSource(
      final String connectionUrl,
      final Map<String, String> connectionProperties,
      final UserCredentials userCredentials) {
    return new SimpleDatabaseConnectionSource(connectionUrl, connectionProperties, userCredentials);
  }

  public static DatabaseConnectionSource newDatabaseConnectionSource(
      final String connectionUrl, final UserCredentials userCredentials) {
    return newDatabaseConnectionSource(connectionUrl, null, userCredentials);
  }

  private DatabaseConnectionSources() {
    // Prevent instantiation
  }
}
