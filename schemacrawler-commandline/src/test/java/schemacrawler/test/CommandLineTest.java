package schemacrawler.test;


import static java.nio.file.Files.newBufferedWriter;
import static schemacrawler.test.utility.TestUtility.createTempFile;
import static sf.util.Utility.UTF8;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;

public class CommandLineTest
  extends BaseDatabaseTest
{

  private static final String COMMAND_LINE_OUTPUT = "command_line_output/";

  @Test
  public void commandLineOverridesWithConfig()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("tables", ".*");
    args.put("routines", ".*");
    args.put("sequences", ".*");
    args.put("synonyms", ".*");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.table.pattern.include", ".*");
    config.put("schemacrawler.table.pattern.exclude", ".*A.*");
    config.put("schemacrawler.routine.pattern.include", ".*");
    config.put("schemacrawler.routine.pattern.exclude", ".*A.*");
    config.put("schemacrawler.sequence.pattern.include", ".*");
    config.put("schemacrawler.sequence.pattern.exclude", "");
    config.put("schemacrawler.synonym.pattern.include", ".*");
    config.put("schemacrawler.synonym.pattern.exclude", "");

    run(args, config, "commandLineOverridesWithConfig.txt");
  }

  @Test
  public void commandLineWithConfig()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_unqualified_names", "true");
    config.put("schemacrawler.table.pattern.include", ".*");
    config.put("schemacrawler.table.pattern.exclude", ".*A.*");
    config.put("schemacrawler.routine.pattern.include", ".*");
    config.put("schemacrawler.routine.pattern.exclude", ".*A.*");
    config.put("schemacrawler.sequence.pattern.include", ".*");
    config.put("schemacrawler.sequence.pattern.exclude", "");
    config.put("schemacrawler.synonym.pattern.include", ".*");
    config.put("schemacrawler.synonym.pattern.exclude", "");

    run(args, config, "commandLineWithConfig.txt");
  }

  @Test
  public void commandLineWithDefaults()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("portablenames", "true");
    // Testing all tables, routines
    // Testing no sequences, synonyms

    run(args, null, "commandLineWithDefaults.txt");
  }

  @Test
  public void commandLineWithNonDefaults()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("portablenames", "true");
    args.put("tables", "");
    args.put("routines", "");
    args.put("sequences", ".*");
    args.put("synonyms", ".*");

    run(args, null, "commandLineWithNonDefaults.txt");
  }

  private Path createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = "SchemaCrawler.TestCommandLineConfig";
    final Path configFile = createTempFile(prefix, "properties");
    final Properties configProperties = new Properties();
    configProperties.putAll(config);
    configProperties.store(newBufferedWriter(configFile, UTF8), prefix);
    return configFile;
  }

  private void run(final Map<String, String> args,
                   final Map<String, String> config,
                   final String referenceFile)
    throws Exception
  {

    try (final TestWriter out = new TestWriter("text");)
    {
      args.put("driver", "org.hsqldb.jdbc.JDBCDriver");
      args.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
      args.put("user", "sa");
      args.put("password", "");
      args.put("noinfo", "true");
      args.put("infolevel", "maximum");
      args.put("command", "brief");
      args.put("outputformat", "text");
      args.put("outputfile", out.toString());

      final Config runConfig = new Config();
      final Config informationSchema = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
      runConfig.putAll(informationSchema);
      if (config != null)
      {
        runConfig.putAll(config);
      }

      final Path configFile = createConfig(runConfig);
      args.put("g", configFile.toString());

      final List<String> argsList = new ArrayList<>();
      for (final Map.Entry<String, String> arg: args.entrySet())
      {
        argsList.add(String.format("-%s=%s", arg.getKey(), arg.getValue()));
      }

      Main.main(argsList.toArray(new String[0]));

      out.assertEquals(COMMAND_LINE_OUTPUT + referenceFile);
    }
  }

}
