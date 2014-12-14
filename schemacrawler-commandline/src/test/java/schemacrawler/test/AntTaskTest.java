/*
 * SchemaCrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package schemacrawler.test;


import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.createTempFile;
import static sf.util.Utility.UTF8;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildFileTest;
import org.apache.tools.ant.Project;
import org.junit.Test;

import schemacrawler.test.utility.TestDatabase;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.options.TextOutputFormat;

public class AntTaskTest
  extends BuildFileTest
{

  private static final String ANT_TEST_OUTPUT = "ant_test_output/";

  private Path buildFile;

  @Override
  public void setUp()
    throws Exception
  {
    buildFile = TestUtility.copyResourceToTempFile("/build.xml");
    configureProject(buildFile.toString());

    TestDatabase.initialize();
  }

  @Test
  public void testAntTask1()
    throws Exception
  {
    // No config file
    final String referenceFile = "ant_task_test.1.txt";
    ant(referenceFile);
  }

  @Test
  public void testAntTask2()
    throws Exception
  {

    final Path configFile = createTempFile("testAntTask2", "properties");
    final Properties configProperties = new Properties();
    configProperties.setProperty("schemacrawler.format.show_unqualified_names",
                                 "true");
    configProperties
      .setProperty("schemacrawler.table.pattern.exclude", ".*A.*");
    configProperties.store(newBufferedWriter(configFile,
                                             UTF8,
                                             WRITE,
                                             CREATE,
                                             TRUNCATE_EXISTING),
                           "testAntTask2");
    setAntProjectProperty("config", configFile.toString());

    final String referenceFile = "ant_task_test.2.txt";
    ant(referenceFile);

  }

  private void ant(final String referenceFile)
    throws IOException, Exception
  {
    final Path testOutputFile = createTempFile(referenceFile, "text");

    setAntProjectProperty("outputfile", testOutputFile.toString());
    executeTarget("ant_task_test");

    // System.out.println(getFullLog());
    // System.out.println(getOutput());

    final List<String> failures = compareOutput(ANT_TEST_OUTPUT + referenceFile,
                                                testOutputFile,
                                                TextOutputFormat.text
                                                  .getFormat());
    if (!failures.isEmpty())
    {
      fail(failures.toString());
    }
  }

  private void setAntProjectProperty(final String name, final String value)
  {
    final Project antProject = getProject();
    antProject.setProperty(name, value);
  }

}
