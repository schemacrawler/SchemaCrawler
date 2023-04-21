/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.move;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.size;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.condition.JRE.JAVA_8;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnPrivilegesRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static us.fatehi.utility.IOUtility.isFileReadable;
import static us.fatehi.utility.Utility.isBlank;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.condition.JRE;
import org.opentest4j.TestAbortedException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.ioresource.InputResource;

public final class TestUtility {

  public static void clean(final String dirname) throws Exception {
    FileUtils.deleteDirectory(
        buildDirectory().resolve("unit_tests_results_output").resolve(dirname).toFile());
  }

  public static List<String> compareCompressedOutput(
      final String referenceFile, final Path testOutputTempFile, final String outputFormat)
      throws Exception {
    return compareOutput(referenceFile, testOutputTempFile, outputFormat, true);
  }

  public static List<String> compareOutput(
      final String referenceFile, final Path testOutputTempFile, final String outputFormat)
      throws Exception {
    return compareOutput(referenceFile, testOutputTempFile, outputFormat, false);
  }

  public static List<String> compareOutput(
      final String referenceFile,
      final Path testOutputTempFile,
      final String outputFormat,
      final boolean isCompressed)
      throws Exception {

    requireNonNull(referenceFile, "Reference file is not defined");
    requireNonNull(testOutputTempFile, "Output file is not defined");
    requireNonNull(outputFormat, "Output format is not defined");

    if (!isFileReadable(testOutputTempFile)) {
      return Collections.singletonList("Output file not created - " + testOutputTempFile);
    }

    final List<String> failures = new ArrayList<>();

    final boolean contentEquals;
    final Reader referenceReader = readerForResource(referenceFile, UTF_8, isCompressed);
    if (referenceReader == null) {
      failures.add("Reference file not available, " + referenceFile);
      contentEquals = false;
    } else if ("png".equals(outputFormat)) {
      contentEquals = true;
    } else {
      final Reader fileReader = readerForFile(testOutputTempFile, isCompressed);
      final Predicate<String> linesFilter = new SvgElementFilter().and(new NeuteredLinesFilter());
      final Function<String, String> neuterMap = new NeuteredExpressionsFilter();
      contentEquals = contentEquals(referenceReader, fileReader, failures, linesFilter, neuterMap);
    }

    if ("html".equals(outputFormat)) {
      validateXML(testOutputTempFile, failures);
    }
    if ("htmlx".equals(outputFormat)) {
      validateXML(testOutputTempFile, failures);
    } else if ("png".equals(outputFormat)) {
      validateDiagram(testOutputTempFile);
    }

    if (!contentEquals) {
      // Reset System streams
      System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
      System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));

      final Path testOutputTargetFilePath =
          buildDirectory().resolve("unit_tests_results_output").resolve(referenceFile);
      createDirectories(testOutputTargetFilePath.getParent());
      deleteIfPossible(testOutputTargetFilePath);
      move(testOutputTempFile, testOutputTargetFilePath, REPLACE_EXISTING);

      System.err.printf(
          "%nOutput does not match - actual output in%n%s%n", testOutputTargetFilePath);
    } else {
      delete(testOutputTempFile);
    }

    return failures;
  }

  public static Path copyResourceToTempFile(final String resource) throws IOException {
    if (isBlank(resource)) {
      throw new IOException("Cannot read empty resource");
    }

    try (final InputStream resourceStream = TestUtility.class.getResourceAsStream(resource)) {
      requireNonNull(resourceStream, "Resource not found, " + resource);
      return writeToTempFile(resourceStream);
    }
  }

  public static void deleteIfPossible(final Path testOutputTargetFilePath) {
    try {
      deleteIfExists(testOutputTargetFilePath);
    } catch (final IOException e) {
      // Ignore exception
    }
  }

  public static <V> V failTestSetup(final String message) {
    testAborted(message, null);
    return null;
  }

  public static <V> V failTestSetup(final String message, final Exception e) {
    testAborted(message, e);
    return null;
  }

  public static String fileHeaderOf(final Path tempFile) throws IOException {
    try (final FileInputStream fileInputStream = new FileInputStream(tempFile.toFile());
        final FileChannel fc = fileInputStream.getChannel()) {
      final ByteBuffer bb = ByteBuffer.allocate(2);
      fc.read(bb);
      final String hexValue = new BigInteger(1, bb.array()).toString(16);
      return hexValue.toUpperCase();
    }
  }

  public static String[] flattenCommandlineArgs(final Map<String, String> argsMap) {
    final List<String> argsList = new ArrayList<>();
    for (final Map.Entry<String, String> arg : argsMap.entrySet()) {
      final String key = arg.getKey();
      final String value = arg.getValue();
      if (value != null) {
        argsList.add(String.format("%s=%s", key, value));
      } else {
        argsList.add(String.format("%s", key));
      }
    }
    final String[] args = argsList.toArray(new String[0]);
    return args;
  }

  public static String javaVersion() {
    if (JRE.currentVersion() == JAVA_8) {
      return "8";
    } else {
      return "LTE";
    }
  }

  /**
   * Loads a properties file.
   *
   * @param inputResource Properties resource.
   * @return Properties
   */
  public static Properties loadProperties(final InputResource inputResource) {
    requireNonNull(inputResource, "No input resource provided");

    try (final Reader reader = inputResource.openNewInputReader(UTF_8); ) {
      final Properties properties = new Properties();
      properties.load(reader);
      return properties;
    } catch (final IOException e) {
      return new Properties();
    }
  }

  public static SchemaRetrievalOptions newSchemaRetrievalOptions() throws IOException {
    final Map<String, String> config = loadHsqldbConfig();

    final InformationSchemaViewsBuilder builder = InformationSchemaViewsBuilder.builder();

    for (final InformationSchemaKey informationSchemaKey : InformationSchemaKey.values()) {
      final String lookupKey =
          String.format("select.%s.%s", informationSchemaKey.getType(), informationSchemaKey);
      if (config.containsKey(lookupKey)) {
        try {
          builder.withSql(informationSchemaKey, config.get(lookupKey));
        } catch (final IllegalArgumentException e) {
          // Ignore
        }
      }
    }
    final InformationSchemaViews informationSchemaViews = builder.toOptions();

    return SchemaRetrievalOptionsBuilder.builder()
        .withInformationSchemaViews(informationSchemaViews)
        .with(tableColumnPrivilegesRetrievalStrategy, data_dictionary_all)
        .toOptions();
  }

  public static Reader readerForResource(final String resource, final Charset encoding)
      throws IOException {
    return readerForResource(resource, encoding, false);
  }

  public static Path savePropertiesToTempFile(final Properties properties) throws IOException {
    requireNonNull(properties, "No properties provided");
    final Path propertiesFile = Files.createTempFile("schemacrawler", ".properties");
    final Writer writer =
        newBufferedWriter(propertiesFile, UTF_8, WRITE, CREATE, TRUNCATE_EXISTING);
    properties.store(writer, "Temporary file to hold properties");
    return propertiesFile;
  }

  public static void validateDiagram(final Path diagramFile) throws IOException {
    assertThat("Diagram file not created", exists(diagramFile), is(true));
    assertThat("Diagram file has 0 bytes size", size(diagramFile), greaterThan(0L));
  }

  public static Path writeStringToTempFile(final String data) throws IOException {

    final Path tempFile =
        IOUtility.createTempFilePath("resource", "data").normalize().toAbsolutePath();
    if (data == null) {
      return tempFile;
    }

    final NeuteredExpressionsFilter neuteredExpressionsFilter = new NeuteredExpressionsFilter();
    final String filteredData = neuteredExpressionsFilter.apply(data);
    Files.write(tempFile, filteredData.getBytes(StandardCharsets.UTF_8));

    return tempFile;
  }

  private static Path buildDirectory() throws Exception {
    final StackTraceElement ste = currentMethodStackTraceElement();
    final Class<?> callingClass = Class.forName(ste.getClassName());
    final Path codePath =
        Paths.get(callingClass.getProtectionDomain().getCodeSource().getLocation().toURI())
            .normalize()
            .toAbsolutePath();
    final boolean isInTarget = codePath.toString().contains("target");
    if (!isInTarget) {
      throw new RuntimeException("Not in build directory, " + codePath);
    }
    final Path directory = codePath.resolve("..");
    return directory.normalize().toAbsolutePath();
  }

  private static boolean contentEquals(
      final Reader expectedInputReader,
      final Reader actualInputReader,
      final List<String> failures,
      final Predicate<String> keepLines,
      final Function<String, String> neuterMap)
      throws Exception {
    if (expectedInputReader == null || actualInputReader == null) {
      return false;
    }

    try (final Stream<String> expectedLinesStream =
            new BufferedReader(expectedInputReader).lines();
        final Stream<String> actualLinesStream = new BufferedReader(actualInputReader).lines()) {

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
      } else if (expectedLinesIterator.hasNext()) {
        failures.add(lineMiscompare(expectedLinesIterator.next(), "<<end of stream>>"));
        return false;
      } else {
        return true;
      }
    }
  }

  private static StackTraceElement currentMethodStackTraceElement() {
    final Pattern baseTestClassName = Pattern.compile(".*\\.Base.*Test");
    final Pattern testClassName = Pattern.compile(".*\\.[A-Z].*Test");

    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (final StackTraceElement stackTraceElement : stackTrace) {
      final String className = stackTraceElement.getClassName();
      if (testClassName.matcher(className).matches()
          && !baseTestClassName.matcher(className).matches()) {
        return stackTraceElement;
      }
    }

    return null;
  }

  private static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest)
      throws IOException {
    final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
    while (src.read(buffer) != -1) {
      // prepare the buffer to be drained
      buffer.flip();
      // write to the channel, may block
      dest.write(buffer);
      // If partial transfer, shift remainder down
      // If buffer is empty, same as doing clear()
      buffer.compact();
    }
    // EOF will leave buffer in fill state
    buffer.flip();
    // make sure the buffer is fully drained.
    while (buffer.hasRemaining()) {
      dest.write(buffer);
    }
  }

  private static String lineMiscompare(final String expectedline, final String actualLine) {
    final StringBuilder buffer = new StringBuilder();
    buffer.append(">> expected followed by actual:").append("\n");
    buffer.append(expectedline).append("\n");
    buffer.append(actualLine).append("\n");

    final String lineMiscompare = buffer.toString();
    return lineMiscompare;
  }

  private static Reader openNewCompressedInputReader(
      final InputStream inputStream, final Charset charset) throws IOException {
    final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
    zipInputStream.getNextEntry();
    return new InputStreamReader(zipInputStream, charset);
  }

  private static Reader readerForFile(final Path testOutputTempFile) throws IOException {
    return readerForFile(testOutputTempFile, false);
  }

  private static Reader readerForFile(final Path testOutputTempFile, final boolean isCompressed)
      throws IOException {

    final BufferedReader bufferedReader;
    if (isCompressed) {
      final ZipInputStream inputStream =
          new ZipInputStream(newInputStream(testOutputTempFile, StandardOpenOption.READ));
      inputStream.getNextEntry();

      bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
    } else {
      bufferedReader = newBufferedReader(testOutputTempFile, UTF_8);
    }
    return bufferedReader;
  }

  private static Reader readerForResource(
      final String resource, final Charset encoding, final boolean isCompressed)
      throws IOException {
    final InputStream inputStream = TestUtility.class.getResourceAsStream("/" + resource);
    final Reader reader;
    if (inputStream != null) {
      final Charset charset;
      if (encoding == null) {
        charset = UTF_8;
      } else {
        charset = encoding;
      }
      if (isCompressed) {
        reader = openNewCompressedInputReader(inputStream, charset);
      } else {
        reader = new InputStreamReader(inputStream, charset);
      }
    } else {
      reader = null;
    }
    return reader;
  }

  private static void testAborted(final String message, final Exception e) {
    throw new TestAbortedException(message, e);
  }

  private static void validateXML(final Path testOutputFile, final List<String> failures)
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
    builder.parse(new InputSource(readerForFile(testOutputFile)));
  }

  private static Path writeToTempFile(final InputStream resourceStream) throws IOException {
    final Path tempFile =
        IOUtility.createTempFilePath("resource", "data").normalize().toAbsolutePath();

    try (final OutputStream tempFileStream =
        newOutputStream(tempFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
      fastChannelCopy(Channels.newChannel(resourceStream), Channels.newChannel(tempFileStream));
    }

    return tempFile;
  }

  private TestUtility() {
    // Prevent instantiation
  }
}
