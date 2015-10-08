package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static sf.util.commandlineparser.CommandLineUtility.flattenCommandlineArgs;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

public class TestSqliteDistribution
{

  private DatabaseConnector dbConnector;

  @Before
  public void setup()
    throws SchemaCrawlerException
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    dbConnector = registry.lookupDatabaseSystemIdentifier("sqlite");
  }

  @Test
  public void testIdentifierQuoteString()
    throws Exception
  {
    assertEquals("\"", dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
      .toOptions().getIdentifierQuoteString());
  }

  @Test
  public void testSqliteMain()
    throws Exception
  {
    try (final TestWriter out = new TestWriter("text");)
    {
      final OutputFormat outputFormat = TextOutputFormat.text;

      final Path sqliteDbFile = copyResourceToTempFile("/sc.db");

      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("server", "sqlite");
      argsMap.put("database", sqliteDbFile.toString());
      argsMap.put("command", "details,dump,count");
      argsMap.put("infolevel", "detailed");
      argsMap.put("outputfile", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));

      out.assertEquals("sqlite.main" + "." + outputFormat.getFormat());
    }
  }

  @Test
  public void testSupports()
    throws Exception
  {
    assertTrue(!dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
      .toOptions().hasOverrideForSupportsCatalogs());
    assertTrue(!dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
      .toOptions().hasOverrideForSupportsSchemas());
  }

}
