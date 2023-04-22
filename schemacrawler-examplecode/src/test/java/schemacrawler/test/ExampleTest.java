/*
 * ========================================================================
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>. All rights reserved.
 * ------------------------------------------------------------------------
 *
 * SchemaCrawler is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * SchemaCrawler and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0, GNU General Public License v3 or GNU Lesser General Public License v3.
 *
 * You may elect to redistribute this code under any of these licenses.
 *
 * The Eclipse Public License is available at: http://www.eclipse.org/legal/epl-v10.html
 *
 * The GNU General Public License v3 and the GNU Lesser General Public License v3 are available at:
 * http://www.gnu.org/licenses/
 *
 * ========================================================================
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.example.ApiExample;
import com.example.ConnectionCheck;
import com.example.ExecutableExample;
import com.example.ResultSetExample;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.testdb.TestDatabase;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;

@CaptureSystemStreams
public class ExampleTest {

  private static TestDatabase testDatabase;

  @BeforeAll
  public static void startDatabase() {
    testDatabase = TestDatabase.initializeStandard();
  }

  @AfterAll
  public static void stopDatabase() {
    testDatabase.stop();
  }

  @Test
  public void apiExample(final CapturedSystemStreams streams) throws Exception {
    ApiExample.main(new String[0]);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(outputOf(streams.out()), hasSameContentAs(classpathResource("ApiExample.txt")));
  }

  @Test
  public void connectionCheck(final CapturedSystemStreams streams) throws Exception {
    ConnectionCheck.main(new String[0]);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(outputOf(streams.out()), hasSameContentAs(classpathResource("ConnectionCheck.txt")));
  }

  @Test
  public void executableExample(final CapturedSystemStreams streams) throws Exception {
    // Test
    final Path tempFile = Files.createTempFile("sc", ".out").toAbsolutePath();
    ExecutableExample.main(new String[] {tempFile.toString()});

    assertThat(outputOf(streams.err()), hasNoContent());

    final List<String> failures =
        compareOutput("ExecutableExample.html", tempFile, TextOutputFormat.html.name());
    if (failures.size() > 0) {
      fail(failures.toString());
    }
  }

  @Test
  public void resultSetExample(final CapturedSystemStreams streams) throws Exception {
    ResultSetExample.main(new String[0]);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()), hasSameContentAs(classpathResource("ResultSetExample.txt")));
  }
}
