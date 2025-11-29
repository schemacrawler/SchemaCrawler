/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility.extensions;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.move;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.jupiter.api.Assertions.fail;
import static us.fatehi.test.utility.TestUtility.buildDirectory;
import static us.fatehi.test.utility.TestUtility.deleteIfPossible;
import static us.fatehi.test.utility.Utility.requireNotBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import us.fatehi.test.utility.SvgElementFilter;
import us.fatehi.test.utility.TestOutputCapture;

public class FileHasContent extends BaseMatcher<ResultsResource> {

  public static ResultsResource classpathResource(final String classpathResource) {
    requireNotBlank(classpathResource, "No classpath resource provided");
    return ResultsResource.fromClasspath(classpathResource);
  }

  public static String contentsOf(final TestOutputCapture testoutput) {
    requireNonNull(testoutput, "No test output capture provided");
    return testoutput.getContents();
  }

  public static FileHasContent hasNoContent() {
    return new FileHasContent(null, null);
  }

  public static FileHasContent hasSameContentAndTypeAs(
      final ResultsResource classpathTestResource, final String outputFormatValue) {
    return new FileHasContent(classpathTestResource, outputFormatValue);
  }

  public static FileHasContent hasSameContentAs(final ResultsResource classpathTestResource) {
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

  public static Path text(final String data) throws IOException {

    final Path tempFile = Files.createTempFile("resource", "data").normalize().toAbsolutePath();
    if (data == null) {
      return tempFile;
    }

    final NeuteredExpressionsFilter neuteredExpressionsFilter = new NeuteredExpressionsFilter();
    final String filteredData = neuteredExpressionsFilter.apply(data);
    Files.write(tempFile, filteredData.getBytes(StandardCharsets.UTF_8));

    return tempFile;
  }

  private static String lineMiscompare(final String expectedline, final String actualLine) {
    final StringBuilder buffer = new StringBuilder();
    buffer.append("was (expected followed by actual)").append(System.lineSeparator());
    buffer.append(expectedline).append(System.lineSeparator());
    buffer.append(actualLine);

    final String lineMiscompare = buffer.toString();
    return lineMiscompare;
  }

  private final ResultsResource expectedResults;
  private final String outputFormatValue;
  // Mutable state, per match run
  private List<String> failures;
  private ResultsResource actualResults;

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

      actualResults = (ResultsResource) actualValue;

      // Reset failures list for this match result
      failures = new ArrayList<>();

      // No file comparison can be done for binary file types,
      // so as long as the output file is present, return
      if ("png".equals(outputFormatValue)) {
        return actualResults.isAvailable();
      }

      validateResults();
      compareOutput();
      cleanup();

      final boolean matches = failures.isEmpty();
      // Print failures for easy reading of build log
      if (!matches) {
        System.err.println(String.join(System.lineSeparator(), failures));
      }
      return matches;

    } catch (final Exception e) {
      return fail(e);
    }
  }

  private void cleanup() {
    // -- Clean up
    // Delete output file if possible
    final Path testOutputTempFile = Path.of(actualResults.getResourceString());
    deleteIfPossible(testOutputTempFile);
    // Flush System streams to prepare for further runs
    System.out.flush();
    System.err.flush();
  }

  private void compareOutput() throws Exception {

    // If there is no expected output, also make sure that
    // the actual output has no contents
    if (expectedResults.isNone()) {
      // File contents should NOT be available
      if (actualResults.isAvailable()) {
        failures.add("output file is not empty");
      }
      return;
    }

    // Check if output is available
    if (!actualResults.isAvailable()) {
      failures.add("output file is not created");
      return;
    }

    final boolean contentEquals;
    if (!expectedResults.isAvailable()) {
      failures.add("reference file is not available");
      contentEquals = false;
    } else if ("json".equalsIgnoreCase(outputFormatValue)) {
      contentEquals = jsonEquals();
    } else {
      contentEquals = contentEquals();
    }

    if (!contentEquals) {
      moveActualToExpected();
    }
  }

  private boolean contentEquals() throws Exception {

    final Predicate<String> keepLines = new SvgElementFilter().and(new NeuteredLinesFilter());
    final Function<String, String> neuterMap = new NeuteredExpressionsFilter();

    try (final BufferedReader expectedResultsReader = expectedResults.openNewReader();
        final BufferedReader actualResultsReader = actualResults.openNewReader();
        final Stream<String> expectedLinesStream = expectedResultsReader.lines();
        final Stream<String> actualLinesStream = actualResultsReader.lines()) {

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

  private boolean jsonEquals() throws Exception {
    try (final BufferedReader expectedResultsReader = expectedResults.openNewReader();
        final BufferedReader actualResultsReader = actualResults.openNewReader(); ) {

      final ObjectMapper mapper = new ObjectMapper();
      final JsonNode expectedJson = mapper.readTree(expectedResultsReader);
      final JsonNode actualJson = mapper.readTree(actualResultsReader);

      final boolean jsonEquals = expectedJson.equals(actualJson);
      if (!jsonEquals) {
        failures.add("Actual JSON data is not what is expected");
      }
      return jsonEquals;
    }
  }

  private void moveActualToExpected() throws Exception {

    final Path testOutputTempFile = Path.of(actualResults.getResourceString());
    final String expectedResultsResource = expectedResults.getResourceString();

    final Path buildDirectory = buildDirectory();
    final Path testOutputTargetFilePath =
        buildDirectory.resolve("unit_tests_results_output").resolve(expectedResultsResource);
    createDirectories(testOutputTargetFilePath.getParent());
    deleteIfPossible(testOutputTargetFilePath);
    move(testOutputTempFile, testOutputTargetFilePath, REPLACE_EXISTING);

    final String relativePathToTestResultsOutput =
        buildDirectory
            .getParent()
            .getParent()
            .relativize(testOutputTargetFilePath)
            .toString()
            .replace('\\', '/');
    failures.add(">> Actual output in:%n%s".formatted(relativePathToTestResultsOutput));
  }

  private void validateResults() throws Exception {
    if ("html".equals(outputFormatValue) || "htmlx".equals(outputFormatValue)) {
      validateXML();
    }
  }

  private void validateXML() throws Exception {
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
}
