/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

public final class MariaDBTestUtility {

  public static JdbcDatabaseContainer<?> newMariaDBContainer() {
    return newMariaDBContainer("11.6.2-noble");
  }

  @SuppressWarnings("resource")
  private static JdbcDatabaseContainer<?> newMariaDBContainer(final String version) {
    final DockerImageName imageName = DockerImageName.parse(MariaDBContainer.NAME);
    return new MariaDBContainer<>(imageName.withTag(version))
        .withEnv("MARIADB_DATABASE", "books")
        .withEnv("MARIADB_ROOT_USER", "root")
        .withEnv("MARIADB_ROOT_PASSWORD", "schemacrawler")
        .withEnv("MARIADB_USER", "schemacrawler")
        .withEnv("MARIADB_PASSWORD", "schemacrawler")
        .withDatabaseName("books")
        .withUsername("root")
        .withPassword("schemacrawler");
  }

  private MariaDBTestUtility() {
    // Prevent instantiation
  }
}
