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

import org.testcontainers.containers.Db2Container;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

public final class DB2TestUtility {

  public static JdbcDatabaseContainer<?> newDB2Container() {
    return newDB2Container("11.5.9.0");
  }

  @SuppressWarnings("resource")
  private static JdbcDatabaseContainer<?> newDB2Container(final String version) {
    final DockerImageName imageName = DockerImageName.parse("icr.io/db2_community/db2");

    final String DBNAME = "schcrwlr";
    final String DB2INST1 = "books";
    final String DB2INST1_PASSWORD = "SchemaCrawler";

    return new Db2Container(imageName.withTag(version))
        .acceptLicense()
        // DBNAME
        .withDatabaseName(DBNAME)
        .withEnv("DBNAME", DBNAME)
        // DB2INSTANCE
        .withUsername(DB2INST1)
        .withEnv("DB2INSTANCE", DB2INST1)
        // DB2INST1_PASSWORD
        .withPassword(DB2INST1_PASSWORD)
        .withEnv("DB2INST1_PASSWORD", DB2INST1_PASSWORD)
        // Other settings
        .withEnv("TO_CREATE_SAMPLEDB", "false")
        .withEnv("REPODB", "false");
  }

  private DB2TestUtility() {
    // Prevent instantiation
  }
}
