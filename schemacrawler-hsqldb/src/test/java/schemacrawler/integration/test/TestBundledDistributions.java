package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.TestDatabase;
import schemacrawler.tools.hsqldb.BundledDriverOptions;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.utility.SchemaCrawlerUtility;

public class TestBundledDistributions
{
  private static TestDatabase testDatabase = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testDatabase.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testDatabase.startDatabase(true);
  }

  @Test
  public void testHsqldbMain()
    throws Exception
  {

    final File testConfigFile = File.createTempFile("schemacrawler.test.",
                                                    ".properties");
    final FileWriter writer = new FileWriter(testConfigFile);
    final Properties properties = new Properties();
    properties
      .setProperty("hsqldb.tables",
                   "SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS FROM INFORMATION_SCHEMA.SYSTEM_TABLES");
    properties.store(writer, "testHsqldbMain");
    writer.close();

    final OutputFormat outputFormat = OutputFormat.text;
    final String referenceFile = "hsqldb.main" + "." + outputFormat.name();
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");
    testOutputFile.delete();

    schemacrawler.tools.hsqldb.Main.main(new String[] {
        "-database=schemacrawler",
        "-user=sa",
        "-password=",
        "-g",
        testConfigFile.getAbsolutePath(),
        "-command=details,dump,count,hsqldb.tables",
        "-infolevel=standard",
        "-outputfile=" + testOutputFile
    });

    final List<String> failures = compareOutput(referenceFile,
                                                testOutputFile,
                                                outputFormat.name());
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
    else
    {
      testConfigFile.delete();
      testOutputFile.delete();
    }

  }

  @Test
  public void testHsqldbWithConnection()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new BundledDriverOptions()
      .getSchemaCrawlerOptions(InfoLevel.maximum);
    final Database database = SchemaCrawlerUtility.getDatabase(testDatabase
      .getConnection(), schemaCrawlerOptions);
    assertNotNull(database);

    assertEquals(6, database.getSchemas().size());
    final Schema schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull(schema);

    assertEquals(6, database.getTables(schema).size());
    final Table table = database.getTable(schema, "AUTHORS");
    assertNotNull(table);

    assertEquals(1, table.getTriggers().size());
    assertNotNull(table.getTrigger("TRG_AUTHORS"));

  }

}
