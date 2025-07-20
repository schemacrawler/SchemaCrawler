/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.clean;
import java.nio.file.Paths;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import schemacrawler.test.utility.FileHasContent;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.ResultsResource;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;

@ResolveTestContext
@TestInstance(Lifecycle.PER_CLASS)
public class FileHasContentTest {

  private static final String TEST_DIR = "_file_has_content/";

  @BeforeAll
  @AfterAll
  public void cleanActualOutput() throws Exception {
    clean(TEST_DIR);
  }

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
      final TestContext testContext, final ResultsResource actual, final FileHasContent matcher)
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
    final ResultsResource expectedFailuresResults =
        ResultsResource.fromClasspath(expectedFailuresResource);
    final ResultsResource actualFailuresResults =
        ResultsResource.fromFilePath(testout.getFilePath());
    final FileHasContent failuresMatcher = FileHasContent.hasSameContentAs(expectedFailuresResults);
    if (!failuresMatcher.matches(actualFailuresResults)) {
      fail("Failure messages are different");
    }
  }
}
