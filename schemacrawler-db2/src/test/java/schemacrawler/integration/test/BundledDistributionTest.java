/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class BundledDistributionTest
{

  @Test
  public void testInformationSchema_db2()
    throws Exception
  {

    final Connection connection = null;
    final DatabaseConnectorRegistry registry =
      DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    final DatabaseConnector databaseSystemIdentifier =
      registry.lookupDatabaseConnector("db2");
    assertThat(databaseSystemIdentifier
                 .getSchemaRetrievalOptionsBuilder(connection)
                 .toOptions()
                 .getInformationSchemaViews()
                 .size(), is(15));
  }

  @Test
  public void testPlugin_db2()
    throws Exception
  {
    final DatabaseConnectorRegistry registry =
      DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    assertThat(registry.hasDatabaseSystemIdentifier("db2"), is(true));
  }

}
