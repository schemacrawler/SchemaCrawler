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

package schemacrawler.test.utility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.nio.file.Paths;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

@ResolveTestContext
public class FileHasContentTest {

  private static final String TEST_DIR = "_file_has_content/";

  @Test
  public void missingOutputFile(final TestContext testContext) throws Exception {
    final String expectedResource = TEST_DIR + testContext.testMethodName();
    assertFailuresFor(
        testContext,
        outputOf(Paths.get("no_such_file")),
        hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void missingReferenceResource(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.println("hello, world");
    }
    final String expectedResource = TEST_DIR + testContext.testMethodName();
    assertFailuresFor(
        testContext, outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void noContentTestWithOutput(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.println("hello, world");
    }
    assertFailuresFor(testContext, outputOf(testout), hasNoContent());
  }

  @Test
  public void outputNotMatching(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.println("hello, world");
    }
    final String expectedResource = TEST_DIR + testContext.testMethodName();
    assertFailuresFor(
        testContext, outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void successfulNoOutputTest(final TestContext testContext) throws Exception {
    assertThat(outputOf(Paths.get("no_such_file")), hasNoContent());
  }

  @Test
  public void successfulTest(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.println("hello, world");
    }
    final String expectedResource = TEST_DIR + testContext.testMethodName();
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  private void assertFailuresFor(
      final TestContext testContext,
      final ResultsResource actual,
      final FileHasContent matcher)
      throws Exception {

    if (matcher.matches(actual)) {
      fail("Expected matcher to fail");
    }

    final Description description = new StringDescription();
    description
        .appendText("Expected: ")
        .appendDescriptionOf(matcher)
        .appendText(System.lineSeparator())
        .appendText("     but: ");
    matcher.describeMismatch(actual, description);

    // Print failures for easy reading of build log
    System.err.println(description);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.println(description);
    }
    final String expectedFailuresResource = TEST_DIR + "failures." + testContext.testMethodName();
    final ResultsResource expectedResults = ResultsResource.fromClasspath(expectedFailuresResource);
    final ResultsResource actualResults = ResultsResource.fromFilePath(testout.getFilePath());
    final FileHasContent failuresMatcher = FileHasContent.hasSameContentAs(expectedResults);
    final List<String> failures = failuresMatcher.compareOutput(actualResults);
    if (!failures.isEmpty()) {
      System.err.println(failures);
      fail(failures.toString());
    }
  }
}
