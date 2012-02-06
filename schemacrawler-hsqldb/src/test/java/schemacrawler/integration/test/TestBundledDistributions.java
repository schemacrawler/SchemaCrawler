package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.hsqldb.BundledDriverOptions;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.utility.SchemaCrawlerUtility;
import schemacrawler.utility.TestDatabase;

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
        "-command=details,dump,count",
        "-infolevel=standard",
        "-outputfile=" + testOutputFile
    });

    final List<String> failures = TestUtility.compareOutput(referenceFile,
                                                            testOutputFile,
                                                            outputFormat);
    if (failures.size() > 0)
    {
      fail(failures.toString());
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

    assertEquals(6, database.getSchemas().length);
    final Schema schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull(schema);

    assertEquals(6, schema.getTables().length);
    final Table table = schema.getTable("AUTHORS");
    assertNotNull(table);

    assertEquals(1, table.getTriggers().length);
    assertNotNull(table.getTrigger("TRG_AUTHORS"));

  }

}
