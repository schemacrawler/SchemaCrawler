package schemacrawler.tools.sqlite.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.io.File;
import java.util.List;

import org.junit.Test;

import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.options.OutputFormat;

public class TestSqliteDistribution
  extends BaseDatabaseTest
{

  @Test
  public void testSqliteMain()
    throws Exception
  {
    final OutputFormat outputFormat = OutputFormat.text;
    final String referenceFile = "sqlite.main" + "." + outputFormat.name();
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");
    testOutputFile.delete();

    final File sqliteDbFile = copyResourceToTempFile("/sc.db");
    schemacrawler.tools.sqlite.Main.main(new String[] {
        "-database=" + sqliteDbFile.getAbsolutePath(),
        "-command=details",
        "-infolevel=standard",
        "-loglevel=ALL",
        "-outputfile=" + testOutputFile
    });

    final List<String> failures = compareOutput(referenceFile,
                                                testOutputFile,
                                                outputFormat.name());
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }

  }

}
