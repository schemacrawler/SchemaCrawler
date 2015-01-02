package schemacrawler.integration.test;


import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static sf.util.commandlineparser.CommandLineArgumentsUtility.flattenCommandlineArgs;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import schemacrawler.Main;
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

}
