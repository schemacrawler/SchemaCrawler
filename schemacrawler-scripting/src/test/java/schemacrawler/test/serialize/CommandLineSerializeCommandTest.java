/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test.serialize;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.oneOf;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.probeFileHeader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.*;
import schemacrawler.tools.integration.serialize.SerializationFormat;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import sf.util.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
@ExtendWith(TestAssertNoSystemErrOutput.class)
@ExtendWith(TestAssertNoSystemOutOutput.class)
public class CommandLineSerializeCommandTest
{

  private TestOutputStream err;
  private TestOutputStream out;

  @AfterEach
  public void cleanUpStreams()
  {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @BeforeEach
  public void setUpStreams()
    throws Exception
  {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

  @Test
  public void commandLineDefault(final TestContext testContext,
                                 final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("-info-level", InfoLevel.standard.name());

    final OutputFormat outputFormat = TextOutputFormat.text;

    final Path testOutputFile = IOUtility.createTempFilePath("test", "");

    commandlineExecution(connectionInfo,
                         "serialize",
                         argsMap,
                         null,
                         outputFormat.getFormat(),
                         testOutputFile);

    assertThat(outputOf(err), hasNoContent());
    assertThat(Files.size(testOutputFile), greaterThan(0L));
    assertThat(probeFileHeader(testOutputFile), is("ACED"));
  }

  @Test
  public void commandLineJava(final TestContext testContext,
                              final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("-info-level", InfoLevel.standard.name());
    argsMap.put("-serialization-format", SerializationFormat.java.name());

    final OutputFormat outputFormat = TextOutputFormat.text;

    final Path testOutputFile = IOUtility.createTempFilePath("test", "");

    commandlineExecution(connectionInfo,
                         "serialize",
                         argsMap,
                         null,
                         outputFormat.getFormat(),
                         testOutputFile);

    assertThat(outputOf(err), hasNoContent());
    assertThat(Files.size(testOutputFile), greaterThan(0L));
    assertThat(probeFileHeader(testOutputFile), is("ACED"));
  }

  @Test
  public void commandLineJson(final TestContext testContext,
                              final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("-info-level", InfoLevel.standard.name());
    argsMap.put("-serialization-format", SerializationFormat.json.name());

    final OutputFormat outputFormat = TextOutputFormat.text;

    final Path testOutputFile = IOUtility.createTempFilePath("test", "");

    commandlineExecution(connectionInfo,
                         "serialize",
                         argsMap,
                         null,
                         outputFormat.getFormat(),
                         testOutputFile);

    assertThat(outputOf(err), hasNoContent());
    assertThat(Files.size(testOutputFile), greaterThan(0L));
    assertThat(probeFileHeader(testOutputFile), is(oneOf("7B0D", "7B0A")));
  }

}
