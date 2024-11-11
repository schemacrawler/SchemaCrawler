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

package schemacrawler.test.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.nio.file.Paths;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.FileHasContent;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestResource;
import schemacrawler.test.utility.TestWriter;

@ResolveTestContext
public class FrameworkTest {

  private static final String TEST_DIR = "framework_test_dir/";

  @Test
  public void successfulTest(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.println("hello, world");
    }
    final String expectedResource = TEST_DIR + testContext.testMethodFullName();
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void missingOutputFile(final TestContext testContext) throws Exception {
    final String expectedResource = TEST_DIR + testContext.testMethodFullName();
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
    final String expectedResource = TEST_DIR + testContext.testMethodFullName();
    assertFailuresFor(
        testContext, outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void outputNotMatching(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.println("hello, world");
    }
    final String expectedResource = TEST_DIR + testContext.testMethodFullName();
    assertFailuresFor(
        testContext, outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  private void assertFailuresFor(
      final TestContext testContext, final TestResource actual, final Matcher<TestResource> matcher)
      throws Exception {

    if (matcher.matches(actual)) {
      fail("Expected matcher to fail");
    }

    final Description description = new StringDescription();
    description
        .appendText(">>>>> ")
        .appendText(testContext.testMethodFullName())
        .appendText(System.lineSeparator())
        .appendText("Expected: ")
        .appendDescriptionOf(matcher)
        .appendText(System.lineSeparator())
        .appendText("     but: ");
    matcher.describeMismatch(actual, description);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.println(description);
    }
    final String expectedFailuresResource =
        TEST_DIR + "failures." + testContext.testMethodFullName();
    final List<String> failures =
        FileHasContent.compareOutput(expectedFailuresResource, testout.getFilePath(), "text");
    if (!failures.isEmpty()) {
      System.err.println(failures);
      fail(failures.toString());
    }
  }
}
