package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.DatabaseSystemConnector;
import schemacrawler.tools.executable.Executable;

public class TestSybaseIQDistribution
{

  private DatabaseSystemConnector databaseSystemConnector;

  @Before
  public void setup()
    throws SchemaCrawlerException
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    final DatabaseConnector databaseSystemIdentifier = registry
      .lookupDatabaseSystemIdentifier("sybaseiq");
    databaseSystemConnector = databaseSystemIdentifier
      .getDatabaseSystemConnector();
  }

  @Test
  public void testIdentifierQuoteString()
    throws Exception
  {
    assertEquals("",
                 databaseSystemConnector
                   .getDatabaseSpecificOverrideOptionsBuilder().toOptions()
                   .getIdentifierQuoteString());
  }

  @Test
  public void testSupports()
    throws Exception
  {
    assertTrue(databaseSystemConnector
      .getDatabaseSpecificOverrideOptionsBuilder().toOptions()
      .hasOverrideForSupportsCatalogs());
    assertTrue(!databaseSystemConnector
      .getDatabaseSpecificOverrideOptionsBuilder().toOptions()
      .isSupportsCatalogs());
    assertTrue(!databaseSystemConnector
      .getDatabaseSpecificOverrideOptionsBuilder().toOptions()
      .hasOverrideForSupportsSchemas());
  }

}
