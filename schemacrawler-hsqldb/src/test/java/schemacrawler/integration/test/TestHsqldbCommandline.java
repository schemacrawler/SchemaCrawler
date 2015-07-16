package schemacrawler.integration.test;


import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static schemacrawler.test.utility.TestUtility.createTempFile;
import static sf.util.commandlineparser.CommandLineUtility.flattenCommandlineArgs;

import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.server.hsqldb.HyperSQLDatabaseConnector;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.databaseconnector.DatabaseSystemConnector;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.utility.SchemaCrawlerUtility;

public class TestHsqldbCommandline
  extends BaseDatabaseTest
{

  @Test
  public void testHsqldbMain()
    throws Exception
  {

    final Path testConfigFile = createTempFile("test", "properties");
    try (final Writer writer = new PrintWriter(newBufferedWriter(testConfigFile,
                                                                 StandardCharsets.UTF_8,
                                                                 WRITE,
                                                                 TRUNCATE_EXISTING,
                                                                 CREATE));)
    {
      final Properties properties = new Properties();
      properties
        .setProperty("hsqldb.tables",
                     "SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS FROM INFORMATION_SCHEMA.SYSTEM_TABLES");
      properties.store(writer, "testHsqldbMain");
    }

    final OutputFormat outputFormat = TextOutputFormat.text;
    try (final TestWriter out = new TestWriter(outputFormat.getFormat());)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("server", "hsqldb");
      argsMap.put("database", "schemacrawler");
      argsMap.put("user", "sa");
      argsMap.put("password", null);
      argsMap.put("g", testConfigFile.toString());
      argsMap.put("command", "details,dump,count,hsqldb.tables");
      argsMap.put("infolevel", "maximum");
      argsMap.put("outputfile", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));

      out.assertEquals("hsqldb.main" + "." + outputFormat.getFormat());
    }
  }

  @Test
  public void testHsqldbWithConnection()
    throws Exception
  {

    final DatabaseSystemConnector hsqldbSystemConnector = new HyperSQLDatabaseConnector()
      .getDatabaseSystemConnector();

    final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions = hsqldbSystemConnector
      .getDatabaseSpecificOverrideOptionsBuilder().toOptions();

    final Config config = hsqldbSystemConnector.getConfig();
    final SchemaCrawlerOptionsBuilder optionsBuilder = new SchemaCrawlerOptionsBuilder()
      .fromConfig(config);
    optionsBuilder.withSchemaInfoLevel(InfoLevel.maximum.getSchemaInfoLevel());

    final Catalog catalog = SchemaCrawlerUtility
      .getCatalog(getConnection(), databaseSpecificOverrideOptions,
                  optionsBuilder.toOptions());
    assertNotNull(catalog);

    assertEquals(6, catalog.getSchemas().size());
    final Schema schema = catalog.getSchema("PUBLIC.BOOKS").orElse(null);
    assertNotNull(schema);

    assertEquals(6, catalog.getTables(schema).size());
    final Table table = catalog.lookupTable(schema, "AUTHORS").orElse(null);
    assertNotNull(table);

    assertEquals(1, table.getTriggers().size());
    assertNotNull(table.getTrigger("TRG_AUTHORS"));

  }

}
