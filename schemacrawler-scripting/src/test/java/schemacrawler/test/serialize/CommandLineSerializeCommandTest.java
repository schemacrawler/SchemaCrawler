/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.oneOf;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.TestUtility.fileHeaderOf;
import static us.fatehi.utility.IOUtility.isFileReadable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.AssertNoSystemErrOutput;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.serialize.options.SerializationFormat;
import us.fatehi.utility.IOUtility;

@WithTestDatabase
@ResolveTestContext
@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
public class CommandLineSerializeCommandTest {

  private static boolean DEBUG = true;

  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestContext testContext)
      throws IOException, URISyntaxException {
    if (directory != null) {
      return;
    }
    directory = testContext.resolveTargetFromRootPath(".");
  }

  @Test
  public void commandLineJava(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThatOutputIsCorrect(
        commandlineSerialize(connectionInfo, SerializationFormat.ser), is("1F8B"));
  }

  @Test
  public void commandLineJson(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThatOutputIsCorrect(
        commandlineSerialize(connectionInfo, SerializationFormat.json), is(oneOf("7B0D", "7B0A")));
  }

  @Test
  public void commandLineYaml(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThatOutputIsCorrect(
        commandlineSerialize(connectionInfo, SerializationFormat.yaml), is("2D2D"));
  }

  private void assertThatOutputIsCorrect(
      final Path testOutputFile, final Matcher<String> fileHeaderMatcher) throws IOException {
    assertThat(isFileReadable(testOutputFile), is(true));
    assertThat(fileHeaderOf(testOutputFile), fileHeaderMatcher);
  }

  private Path commandlineSerialize(
      final DatabaseConnectionInfo connectionInfo, final SerializationFormat serializationFormat)
      throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());

    final Path testOutputFile = IOUtility.createTempFilePath("test", "");

    commandlineExecution(
        connectionInfo,
        "serialize",
        argsMap,
        null,
        serializationFormat.getFormat(),
        testOutputFile);
    if (DEBUG) {
      final Path copied = directory.resolve("serialize." + serializationFormat.getFileExtension());
      Files.copy(testOutputFile, copied, StandardCopyOption.REPLACE_EXISTING);
      // System.out.println(copied.toAbsolutePath());
    }

    return testOutputFile;
  }
}
