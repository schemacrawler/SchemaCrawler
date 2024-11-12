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

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.move;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

public class FileHasContent extends BaseMatcher<ResultsResource> {

  public static ResultsResource classpathResource(final String classpathResource) {
    requireNotBlank(classpathResource, "No classpath resource provided");
    return ResultsResource.fromClasspath(classpathResource);
  }

  public static String contentsOf(final TestOutputCapture testoutput) {
    requireNonNull(testoutput, "No test output capture provided");
    return testoutput.getContents();
  }

  public static Matcher<ResultsResource> hasNoContent() {
    return new FileHasContent(null, null);
  }

  public static Matcher<ResultsResource> hasSameContentAndTypeAs(
      final ResultsResource classpathTestResource, final String outputFormatValue) {
    return new FileHasContent(classpathTestResource, outputFormatValue);
  }

  public static Matcher<ResultsResource> hasSameContentAs(
      final ResultsResource classpathTestResource) {
    return new FileHasContent(classpathTestResource, null);
  }

  public static ResultsResource outputOf(final Path filePath) {
    return ResultsResource.fromFilePath(filePath);
  }

  public static ResultsResource outputOf(final TestOutputCapture testoutput) {
    requireNonNull(testoutput, "No test output capture provided");
    final Path filePath = testoutput.getFilePath();
    return outputOf(filePath);
  }

  static List<String> compareOutput(
      final ResultsResource actualResults,
      final ResultsResource expectedResults,
      final String outputFormatValue)
      throws Exception {

    final List<String> failures = new ArrayList<>();

    // If there is no expected output, also make sure that
    // the actual output has no contents
    if (expectedResults.isNone()) {
      // File contents should NOT be available
      if (actualResults.isAvailable()) {
        failures.add("output file is not empty");
      }
      return failures;
    }

    // Check if output is available
    if (!actualResults.isAvailable()) {
      failures.add("output file not created");
      return failures;
    }

    final boolean contentEquals;
    if (!expectedResults.isAvailable()) {
      failures.add("reference file not available");
      contentEquals = false;
    } else if ("png".equals(outputFormatValue)) {
      contentEquals = true;
    } else {
      final BufferedReader expectedResultsReader = expectedResults.openNewReader();
      final BufferedReader actualResultsReader = actualResults.openNewReader();
      final Predicate<String> linesFilter = new SvgElementFilter().and(new NeuteredLinesFilter());
      final Function<String, String> neuterMap = new NeuteredExpressionsFilter();
      contentEquals =
          contentEquals(
              expectedResultsReader, actualResultsReader, failures, linesFilter, neuterMap);
    }

    // Print failures for easy reading of build log
    if (!failures.isEmpty()) {
      System.err.println(String.join(System.lineSeparator(), failures));
    }

    final Path testOutputTempFile = Paths.get(actualResults.getResourceString());
    if (!contentEquals) {
      final String expectedResultsResource = expectedResults.getResourceString();
      final String relativePathToTestResultsOutput =
          moveActualToExpected(testOutputTempFile, expectedResultsResource);
      failures.add(String.format(">> actual output in:%n%s", relativePathToTestResultsOutput));
    } else {
      TestUtility.deleteIfPossible(testOutputTempFile);
    }

    // Flush System streams to prepare for further runs
    System.out.flush();
    System.err.flush();

    return failures;
  }

  private static boolean contentEquals(
      final BufferedReader expectedInputReader,
      final BufferedReader actualInputReader,
      final List<String> failures,
      final Predicate<String> keepLines,
      final Function<String, String> neuterMap)
      throws Exception {
    if (expectedInputReader == null || actualInputReader == null) {
      return false;
    }

    try (final Stream<String> expectedLinesStream = expectedInputReader.lines();
        final Stream<String> actualLinesStream = actualInputReader.lines()) {

      final Iterator<String> expectedLinesIterator =
          expectedLinesStream.filter(keepLines).map(neuterMap).iterator();
      final Iterator<String> actualLinesIterator =
          actualLinesStream.filter(keepLines).map(neuterMap).iterator();

      while (expectedLinesIterator.hasNext() && actualLinesIterator.hasNext()) {
        final String expectedline = expectedLinesIterator.next();
        final String actualLine = actualLinesIterator.next();

        if (!expectedline.equals(actualLine)) {
          failures.add(lineMiscompare(expectedline, actualLine));
          return false;
        }
      }

      if (actualLinesIterator.hasNext()) {
        failures.add(lineMiscompare("<<end of stream>>", actualLinesIterator.next()));
        return false;
      }
      if (expectedLinesIterator.hasNext()) {
        failures.add(lineMiscompare(expectedLinesIterator.next(), "<<end of stream>>"));
        return false;
      }
      return true;
    }
  }

  private static String lineMiscompare(final String expectedline, final String actualLine) {
    final StringBuilder buffer = new StringBuilder();
    buffer.append("was (expected followed by actual)").append(System.lineSeparator());
    buffer.append(expectedline).append(System.lineSeparator());
    buffer.append(actualLine);

    final String lineMiscompare = buffer.toString();
    return lineMiscompare;
  }

  private static String moveActualToExpected(
      final Path testOutputTempFile, final String expectedResultsResource)
      throws Exception, IOException {
    final Path buildDirectory = TestUtility.buildDirectory();
    final Path testOutputTargetFilePath =
        buildDirectory.resolve("unit_tests_results_output").resolve(expectedResultsResource);
    createDirectories(testOutputTargetFilePath.getParent());
    TestUtility.deleteIfPossible(testOutputTargetFilePath);
    move(testOutputTempFile, testOutputTargetFilePath, REPLACE_EXISTING);

    final String relativePathToTestResultsOutput =
        buildDirectory
            .getParent()
            .getParent()
            .relativize(testOutputTargetFilePath)
            .toString()
            .replace('\\', '/');
    return relativePathToTestResultsOutput;
  }

  private static void validateXML(final ResultsResource actualResults, final List<String> failures)
      throws Exception {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);

    final DocumentBuilder builder = factory.newDocumentBuilder();
    builder.setErrorHandler(
        new ErrorHandler() {
          @Override
          public void error(final SAXParseException e) throws SAXException {
            failures.add(e.getMessage());
          }

          @Override
          public void fatalError(final SAXParseException e) throws SAXException {
            failures.add(e.getMessage());
          }

          @Override
          public void warning(final SAXParseException e) throws SAXException {
            failures.add(e.getMessage());
          }
        });
    try (final Reader reader = actualResults.openNewReader()) {
      builder.parse(new InputSource(reader));
    }
  }

  private final ResultsResource expectedResults;
  private final String outputFormatValue;
  private List<String> failures;

  private FileHasContent(final ResultsResource expectedResults, final String outputFormatValue) {
    if (expectedResults != null) {
      this.expectedResults = expectedResults;
    } else {
      this.expectedResults = ResultsResource.none();
    }

    if (isBlank(outputFormatValue)) {
      this.outputFormatValue = "text";
    } else {
      this.outputFormatValue = outputFormatValue;
    }
  }

  @Override
  public void describeMismatch(final Object item, final Description description) {
    if (!failures.isEmpty()) {
      description.appendText(String.join("\n", failures));
    }
  }

  /** This message is shown in the expected section of the failure. */
  @Override
  public void describeTo(final Description description) {
    if (expectedResults.isNone()) {
      description.appendText("no output");
    } else {
      description.appendText("contents of ").appendValue(expectedResults);
    }
  }

  @Override
  public boolean matches(final Object actualValue) {
    try {
      if (actualValue == null || !(actualValue instanceof ResultsResource)) {
        throw new RuntimeException("No test output provided");
      }
      final ResultsResource actualResults = (ResultsResource) actualValue;

      if ("html".equals(outputFormatValue) || "htmlx".equals(outputFormatValue)) {
        validateXML(actualResults, failures);
      }

      failures = compareOutput(actualResults, expectedResults, outputFormatValue);
      return failures != null && failures.isEmpty();

    } catch (final Exception e) {
      return fail(e);
    }
  }
}
