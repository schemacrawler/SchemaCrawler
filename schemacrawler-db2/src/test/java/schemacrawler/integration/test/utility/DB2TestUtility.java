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
package schemacrawler.integration.test.utility;

import org.testcontainers.containers.Db2Container;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

public final class DB2TestUtility {

  @SuppressWarnings("resource")
  public static JdbcDatabaseContainer<?> newDB211Container() {
    return newDB2Container("11.5.7.0");
  }

  @SuppressWarnings("resource")
  private static JdbcDatabaseContainer<?> newDB2Container(final String version) {
    final DockerImageName imageName = DockerImageName.parse("ibmcom/db2");
    return new Db2Container(imageName.withTag(version)).acceptLicense();
  }

  private DB2TestUtility() {
    // Prevent instantiation
  }
}
