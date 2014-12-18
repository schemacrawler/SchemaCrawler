package schemacrawler.integration.test;


import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.nio.file.Path;

import org.junit.Test;

import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

public class TestSqliteDistribution
{

  @Test
  public void testSqliteMain()
    throws Exception
  {
    try (final TestWriter out = new TestWriter("text");)
    {
      final OutputFormat outputFormat = TextOutputFormat.text;

      final Path sqliteDbFile = copyResourceToTempFile("/sc.db");
      schemacrawler.Main.main(new String[] {
          "-server=sqlite",
          "-database=" + sqliteDbFile.toString(),
          "-command=details,dump,count",
          "-infolevel=detailed",
          "-outputfile=" + out.toString()
      });

      out.assertEquals("sqlite.main" + "." + outputFormat.getFormat());
    }
  }

}
