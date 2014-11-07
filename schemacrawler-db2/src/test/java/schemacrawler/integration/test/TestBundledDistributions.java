package schemacrawler.integration.test;


import static org.junit.Assert.assertTrue;

import org.junit.Test;

import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class TestBundledDistributions
{

  @Test
  public void testPlugin_db2()
    throws Exception
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    assertTrue(registry.hasDatabaseSystemIdentifier("db2"));
  }

}
