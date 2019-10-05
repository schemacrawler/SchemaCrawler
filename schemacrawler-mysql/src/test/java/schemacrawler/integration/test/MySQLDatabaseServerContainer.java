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

package schemacrawler.integration.test;


import org.testcontainers.containers.MySQLContainer;
import schemacrawler.test.utility.BaseDatabaseServerContainer;

public class MySQLDatabaseServerContainer
  extends BaseDatabaseServerContainer<MySQLContainer>
{

  private static MySQLContainer newContainer()
  {
    final MySQLContainer dbContainer = new MySQLContainer<>()
      .withCommand("mysqld", "--lower_case_table_names=1")
      .withUsername("schemacrawler");
    return dbContainer;
  }

  public MySQLDatabaseServerContainer()
  {
    super(newContainer());
  }

  public MySQLDatabaseServerContainer(final String databaseName)
  {
    super(newContainer(), databaseName);
  }

}
