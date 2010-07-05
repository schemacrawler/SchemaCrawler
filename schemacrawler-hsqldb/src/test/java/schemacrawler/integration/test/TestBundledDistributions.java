package schemacrawler.integration.test;


import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.tools.hsqldb.Main;
import schemacrawler.utility.TestDatabase;

public class TestBundledDistributions
{
  private static TestDatabase testUtility = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testUtility.startDatabase();
  }

  @Test
  public void testHsqldbMain()
    throws Exception
  {
    final String referenceFile = "hsqldb.main";
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");
    testOutputFile.delete();

    Main.main(new String[] {
        "-database=schemacrawler",
        "-user=sa",
        "-password=",
        "-command=verbose_schema,dump.count",
        "-outputfile=" + testOutputFile
    });
  }

}
