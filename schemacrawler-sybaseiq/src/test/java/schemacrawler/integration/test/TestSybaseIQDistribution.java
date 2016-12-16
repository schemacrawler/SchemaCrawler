/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class TestSybaseIQDistribution
{

  private DatabaseConnector dbConnector;

  @Before
  public void setup()
    throws SchemaCrawlerException
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    dbConnector = registry.lookupDatabaseConnector("sybaseiq");
  }

  @Test
  public void testIdentifierQuoteString()
    throws Exception
  {
    assertEquals("",
                 dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
                   .toOptions().getIdentifierQuoteString());
  }

  @Test
  public void testSupports()
    throws Exception
  {
    assertTrue(dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
      .toOptions().hasOverrideForSupportsCatalogs());
    assertTrue(!dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
      .toOptions().isSupportsCatalogs());
    assertTrue(!dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
      .toOptions().hasOverrideForSupportsSchemas());
  }

}
