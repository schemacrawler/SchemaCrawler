/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.integration.test.utility;

import java.time.Duration;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

public final class SqlServerTestUtility {

  @SuppressWarnings("resource")
  public static JdbcDatabaseContainer<?> newSqlServer2019Container() {
    return newSqlServerContainer("2019-CU9-ubuntu-18.04");
  }

  @SuppressWarnings("resource")
  private static JdbcDatabaseContainer<?> newSqlServerContainer(final String version) {
    final DockerImageName imageName = DockerImageName.parse("mcr.microsoft.com/mssql/server");
    return new MSSQLServerContainer<>(imageName.withTag(version))
        .withUrlParam("encrypt", "false")
        .acceptLicense()
        .withStartupTimeout(Duration.ofMinutes(3));
  }

  private SqlServerTestUtility() {
    // Prevent instantiation
  }
}
