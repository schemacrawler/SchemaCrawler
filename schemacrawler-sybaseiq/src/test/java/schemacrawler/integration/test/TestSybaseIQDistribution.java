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
    dbConnector = registry.lookupDatabaseSystemIdentifier("sybaseiq");
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
