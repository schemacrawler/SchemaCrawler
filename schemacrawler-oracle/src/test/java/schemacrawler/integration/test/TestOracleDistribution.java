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

public class TestOracleDistribution
{

  private DatabaseSystemConnector databaseSystemConnector;

  @Before
  public void setup()
    throws SchemaCrawlerException
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    final DatabaseConnector databaseSystemIdentifier = registry
      .lookupDatabaseSystemIdentifier("oracle");
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
  public void testPostExecutable()
    throws Exception
  {
    final Executable newPostExecutable = databaseSystemConnector
      .newPostExecutable();
    assertNotNull(newPostExecutable);
    assertEquals("NoOpExecutable",
                 newPostExecutable.getClass().getSimpleName());
  }

  @Test
  public void testPreExecutable()
    throws Exception
  {
    final Executable newPreExecutable = databaseSystemConnector
      .newPreExecutable();
    assertNotNull(newPreExecutable);
    assertEquals("OraclePreExecutable",
                 newPreExecutable.getClass().getSimpleName());
  }

  @Test
  public void testSupports()
    throws Exception
  {
    assertTrue(!databaseSystemConnector
      .getDatabaseSpecificOverrideOptionsBuilder().toOptions()
      .hasOverrideForSupportsCatalogs());
    assertTrue(!databaseSystemConnector
      .getDatabaseSpecificOverrideOptionsBuilder().toOptions()
      .hasOverrideForSupportsSchemas());
  }

}
