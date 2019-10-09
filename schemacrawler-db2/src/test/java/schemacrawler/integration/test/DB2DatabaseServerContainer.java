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


import org.testcontainers.containers.Db2Container;
import schemacrawler.test.utility.BaseDatabaseServerContainer;

public class DB2DatabaseServerContainer
  extends BaseDatabaseServerContainer<Db2Container>
{

  private static Db2Container newContainer()
  {
    final Db2Container dbContainer = new Db2Container().acceptLicense();
    return dbContainer;
  }

  public DB2DatabaseServerContainer()
  {
    super(newContainer());
  }

  public DB2DatabaseServerContainer(final String databaseName)
  {
    super(newContainer(), databaseName);
  }

}
