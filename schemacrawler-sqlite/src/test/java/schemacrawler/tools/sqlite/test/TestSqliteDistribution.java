package schemacrawler.tools.sqlite.test;


import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.options.OutputFormat;

public class TestSqliteDistribution
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

    final File sqliteDbFile = TestUtility.copyResourceToTempFile("/sc.db");
    schemacrawler.tools.sqlite.Main.main(new String[] {
        "-database=" + sqliteDbFile.getAbsolutePath(),
        "-command=details",
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
