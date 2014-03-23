package schemacrawler.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.test.utility.BaseDatabaseTest;

public class CommandLineTest
  extends BaseDatabaseTest
{

  private static final String COMMAND_LINE_OUTPUT = "command_line_output/";

  @Test
  public void commandLine()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("portablenames", "true");

    run(args, null, "commandLine.txt");
  }

  @Test
  public void commandLineOverridesWithConfig()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("tables", ".*");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.table.pattern.exclude", ".*A.*");

    run(args, config, "commandLineOverridesWithConfig.txt");
  }

  @Test
  public void commandLineWithConfig()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_unqualified_names", "true");
    config.put("schemacrawler.table.pattern.exclude", ".*A.*");

    run(args, config, "commandLineWithConfig.txt");
  }

  private File createConfig(final Map<String, String> config)
    throws IOException
  {
    final File configFile = File.createTempFile("SchemaCrawler.testAntTask2",
                                                ".properties");
    final Properties configProperties = new Properties();
    configProperties.putAll(config);
    configProperties.store(new FileWriter(configFile), "test");
    return configFile;
  }

  private void run(final Map<String, String> args,
                   final Map<String, String> config,
                   final String referenceFile)
    throws Exception
  {

    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");
    testOutputFile.delete();

    args.put("driver", "org.hsqldb.jdbc.JDBCDriver");
    args.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
    args.put("user", "sa");
    args.put("password", "");
    args.put("infolevel", "standard");
    args.put("command", "schema");
    args.put("outputformat", "text");
    args.put("outputfile", testOutputFile.getAbsolutePath());

    if (config != null)
    {
      final File configFile = createConfig(config);
      args.put("g", configFile.getAbsolutePath());
    }

    final List<String> argsList = new ArrayList<>();
    for (final Map.Entry<String, String> arg: args.entrySet())
    {
      argsList.add(String.format("-%s=%s", arg.getKey(), arg.getValue()));
    }

    Main.main(argsList.toArray(new String[0]));

    final List<String> failures = new ArrayList<>();
    failures.addAll(compareOutput(COMMAND_LINE_OUTPUT + referenceFile,
                                  testOutputFile));

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
