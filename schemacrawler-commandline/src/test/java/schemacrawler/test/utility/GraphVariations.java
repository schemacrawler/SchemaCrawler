/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package schemacrawler.test.utility;


import static schemacrawler.test.utility.TestUtility.currentMethodName;

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
import org.junit.runner.JUnitCore;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;

public class GraphVariations
  extends BaseDatabaseTest
{

  public static void main(final String[] args)
  {
    JUnitCore.main("schemacrawler.test.utility.GraphVariations");
  }

  private File directory;

  @Test
  public void diagram()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");
    args.put("tables", ".*");
    args.put("routines", "");

    final Map<String, String> config = new HashMap<>();

    run(args, config, new File(directory, currentMethodName() + ".png"));
  }

  @Test
  public void diagram_2_portablenames()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("tables", ".*");
    args.put("routines", "");

    final Map<String, String> config = new HashMap<>();

    run(args, config, new File(directory, currentMethodName() + ".png"));
  }

  @Test
  public void diagram_3_ordinals()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");
    args.put("tables", ".*");
    args.put("routines", "");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", "true");

    run(args, config, new File(directory, currentMethodName() + ".png"));
  }

  @Test
  public void diagram_4_alphabetical()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");
    args.put("sortcolumns", "true");
    args.put("tables", ".*");
    args.put("routines", "");

    final Map<String, String> config = new HashMap<>();

    run(args, config, new File(directory, currentMethodName() + ".png"));
  }

  @Test
  public void diagram_5_grep()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("tables", ".*");
    args.put("tabletypes", "TABLE");
    args.put("routines", "");

    final Map<String, String> config = new HashMap<>();

    run(args, config, new File(directory, currentMethodName() + ".png"));
  }

  @Test
  public void diagram_6_grep_onlymatching()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("only-matching", "true");
    args.put("tables", ".*");
    args.put("tabletypes", "TABLE");
    args.put("routines", "");

    final Map<String, String> config = new HashMap<>();

    run(args, config, new File(directory, currentMethodName() + ".png"));
  }

  @Before
  public void setupDirectory()
    throws IOException
  {
    directory = new File(this.getClass().getProtectionDomain().getCodeSource()
                           .getLocation().getFile().replace("%20", " "),
                         "../../../schemacrawler-site/src/site/resources/images")
      .getCanonicalFile();
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
                   final File outputFile)
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
