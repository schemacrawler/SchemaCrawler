/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.test;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.test.utility.TestWriter;
import sf.util.IOUtility;

public class CommandLineTest
  extends BaseDatabaseTest
{

  private static final String COMMAND_LINE_OUTPUT = "command_line_output/";

  @Rule
  public TestName testName = new TestName();

  @Test
  public void commandLineOverridesWithConfig()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
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

    run(args, config);
  }

  @Test
  public void commandLineRoutinesWithColumnsSorting()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("tables", "");
    args.put("sortcolumns", Boolean.TRUE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(args, null);
  }

  @Test
  public void commandLineRoutinesWithoutColumnsSorting()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("tables", "");
    args.put("sortcolumns", Boolean.FALSE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(args, null);
  }

  @Test
  public void commandLineRoutinesWithoutSorting()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("tables", "");
    args.put("sortroutines", Boolean.FALSE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(args, null);
  }

  @Test
  public void commandLineRoutinesWithSorting()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("tables", "");
    args.put("sortroutines", Boolean.TRUE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(args, null);
  }

  @Test
  public void commandLineTablesWithColumnsSorting()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("routines", "");
    args.put("sortcolumns", Boolean.TRUE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(args, null);
  }

  @Test
  public void commandLineTablesWithoutColumnsSorting()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("routines", "");
    args.put("sortcolumns", Boolean.FALSE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(args, null);
  }

  @Test
  public void commandLineTablesWithoutSorting()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("routines", "");
    args.put("sorttables", Boolean.FALSE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(args, null);
  }

  @Test
  public void commandLineTablesWithSorting()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("routines", "");
    args.put("sorttables", Boolean.TRUE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(args, null);
  }

  @Test
  public void commandLineWithConfig()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_unqualified_names",
               Boolean.TRUE.toString());
    config.put("schemacrawler.table.pattern.include", ".*");
    config.put("schemacrawler.table.pattern.exclude", ".*A.*");
    config.put("schemacrawler.routine.pattern.include", ".*");
    config.put("schemacrawler.routine.pattern.exclude", ".*A.*");
    config.put("schemacrawler.sequence.pattern.include", ".*");
    config.put("schemacrawler.sequence.pattern.exclude", "");
    config.put("schemacrawler.synonym.pattern.include", ".*");
    config.put("schemacrawler.synonym.pattern.exclude", "");

    run(args, config);
  }

  @Test
  public void commandLineWithDefaults()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("portablenames", Boolean.TRUE.toString());
    // Testing all tables, routines
    // Testing no sequences, synonyms

    run(args, null);
  }

  @Test
  public void commandLineWithNonDefaults()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("portablenames", Boolean.TRUE.toString());
    args.put("tables", "");
    args.put("routines", "");
    args.put("sequences", ".*");
    args.put("synonyms", ".*");

    run(args, null);
  }

  private Path createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = "SchemaCrawler.TestCommandLineConfig";
    final Path configFile = IOUtility.createTempFilePath(prefix, "properties");
    final Properties configProperties = new Properties();
    configProperties.putAll(config);
    configProperties.store(newBufferedWriter(configFile, UTF_8), prefix);
    return configFile;
  }

  private void run(final Map<String, String> argsMap,
                   final Map<String, String> config)
    throws Exception
  {

    try (final TestWriter out = new TestWriter("text");)
    {
      argsMap.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
      argsMap.put("user", "sa");
      argsMap.put("password", "");
      argsMap.put("noinfo", Boolean.TRUE.toString());
      argsMap.put("schemas", ".*\\.(?!FOR_LINT).*");
      argsMap.put("infolevel", "maximum");
      argsMap.put("command", "brief");
      argsMap.put("outputformat", "text");
      argsMap.put("outputfile", out.toString());

      final Config runConfig = new Config();
      final Config informationSchema = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
      runConfig.putAll(informationSchema);
      if (config != null)
      {
        runConfig.putAll(config);
      }

      final Path configFile = createConfig(runConfig);
      argsMap.put("g", configFile.toString());

      Main.main(flattenCommandlineArgs(argsMap));

      out.assertEquals(COMMAND_LINE_OUTPUT + testName.currentMethodName()
                       + ".txt");
    }
  }

}
