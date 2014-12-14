package schemacrawler.integration.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static schemacrawler.test.utility.TestUtility.createTempFile;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

public class TestSqliteDistribution
{

  @Test
  public void testSqliteMain()
    throws Exception
  {
    final OutputFormat outputFormat = TextOutputFormat.text;
    final String referenceFile = "sqlite.main" + "." + outputFormat.getFormat();
    final Path testOutputFile = createTempFile(referenceFile, "data");

    final File sqliteDbFile = copyResourceToTempFile("/sc.db");
    schemacrawler.Main.main(new String[] {
        "-server=sqlite",
        "-database=" + sqliteDbFile.getAbsolutePath(),
        "-command=details,dump,count",
        "-infolevel=detailed",
        "-outputfile=" + testOutputFile
    });

    final List<String> failures = compareOutput(referenceFile,
                                                testOutputFile,
                                                outputFormat.getFormat());
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }

  }

}
