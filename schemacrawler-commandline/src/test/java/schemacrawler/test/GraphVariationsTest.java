package schemacrawler.test;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.BaseDatabaseTest;

public class GraphVariationsTest
  extends BaseDatabaseTest
{

  private File directory;

  @Before
  public void setupDirectory()
    throws IOException
  {
    directory = new File(GraphVariationsTest.class.getProtectionDomain()
                           .getCodeSource().getLocation().getFile()
                           .replace("%20", " "),
                         "../../../schemacrawler-site/src/site/resources/images")
      .getCanonicalFile();
  }

  @Test
  public void diagram()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");
    args.put("tables", ".*");
    args.put("routines", "");

    final Map<String, String> config = new HashMap<>();

    run(args, config, new File(directory, "diagram.png"));
  }

  @Test
  public void diagram_portablenames()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("tables", ".*");
    args.put("routines", "");

    final Map<String, String> config = new HashMap<>();

    run(args, config, new File(directory, "diagram_portablenames.png"));
  }

  @Test
  public void diagram_ordinals()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("tables", ".*");
    args.put("routines", "");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", "true");

    run(args, config, new File(directory, "diagram_ordinals.png"));
  }

  private File createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = "SchemaCrawler.TestCommandLineConfig";
    final File configFile = File.createTempFile(prefix, ".properties");
    final Properties configProperties = new Properties();
    configProperties.putAll(config);
    configProperties.store(new FileWriter(configFile), prefix);
    return configFile;
  }

  private void run(final Map<String, String> args,
                   final Map<String, String> config,
                   File outputFile)
    throws Exception
  {
    outputFile.delete();

    args.put("driver", "org.hsqldb.jdbc.JDBCDriver");
    args.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
    args.put("user", "sa");
    args.put("password", "");
    args.put("command", "graph");
    args.put("outputformat", "png");
    args.put("outputfile", outputFile.getAbsolutePath());

    final Config runConfig = new Config();
    final Config informationSchema = Config
      .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
    runConfig.putAll(informationSchema);
    if (config != null)
    {
      runConfig.putAll(config);
    }

    final File configFile = createConfig(runConfig);
    args.put("g", configFile.getAbsolutePath());

    final List<String> argsList = new ArrayList<>();
    for (final Map.Entry<String, String> arg: args.entrySet())
    {
      argsList.add(String.format("-%s=%s", arg.getKey(), arg.getValue()));
    }

    Main.main(argsList.toArray(new String[argsList.size()]));
  }

}
