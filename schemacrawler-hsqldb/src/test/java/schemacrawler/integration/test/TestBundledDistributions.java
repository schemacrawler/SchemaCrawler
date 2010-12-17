package schemacrawler.integration.test;


import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.test.TestUtility;
import schemacrawler.tools.hsqldb.Main;
import schemacrawler.tools.options.OutputFormat;
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
    final OutputFormat outputFormat = OutputFormat.text;
    final String referenceFile = "hsqldb.main" + "." + outputFormat.name();
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");
    testOutputFile.delete();

    Main.main(new String[] {
        "-database=schemacrawler",
        "-user=sa",
        "-password=",
        "-command=verbose_schema,dump,count",
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

}
