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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.jupiter.api.condition.JRE.JAVA_8;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnPrivilegesRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.condition.JRE;
import org.opentest4j.TestAbortedException;
import static java.util.Objects.requireNonNull;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.ioresource.ClasspathInputResource;
import us.fatehi.utility.ioresource.InputResource;

public final class TestUtility {

  public static void clean(final String dirname) throws Exception {
    FileUtils.deleteDirectory(
        buildDirectory().resolve("unit_tests_results_output").resolve(dirname).toFile());
  }

  public static Path copyResourceToTempFile(final String resource) throws IOException {
    final InputResource inputResource = new ClasspathInputResource(resource);
    final Path tempFile =
        IOUtility.createTempFilePath("resource", "data").normalize().toAbsolutePath();
    Files.copy(inputResource.openNewInputStream(), tempFile);

    return tempFile;
  }

  public static ResultSet createMockResultSet(final String[] columnNames, final Object[][] data)
      throws SQLException {
    final ResultSet rs = mock(ResultSet.class);
    final ResultSetMetaData rsmd = mock(ResultSetMetaData.class);

    // Mock ResultSetMetaData
    when(rs.getMetaData()).thenReturn(rsmd);
    when(rsmd.getColumnCount()).thenReturn(columnNames.length);
    for (int i = 0; i < columnNames.length; i++) {
      when(rsmd.getColumnName(i + 1)).thenReturn(columnNames[i]);
    }

    // Mock ResultSet data
    final int[] rowIndex = {-1};
    when(rs.next())
        .thenAnswer(
            invocation -> {
              rowIndex[0]++;
              return rowIndex[0] < data.length;
            });

    for (int i = 0; i < columnNames.length; i++) {
      final int columnIndex = i;
      when(rs.getObject(columnIndex + 1)).thenAnswer(invocation -> data[rowIndex[0]][columnIndex]);
      when(rs.getString(columnIndex + 1))
          .thenAnswer(invocation -> (String) data[rowIndex[0]][columnIndex]);
    }

    return rs;
  }

  public static void deleteIfPossible(final Path testOutputTargetFilePath) {
    try {
      deleteIfExists(testOutputTargetFilePath);
    } catch (final IOException e) {
      // Ignore exception
    }
  }

  public static <V> V failTestSetup(final String message, final Exception e) {
    throw new TestAbortedException(message, e);
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
    }
    return "LTE";
  }

  /**
   * Loads a properties file.
   *
   * @param inputResource Properties resource.
   * @return Properties
   * @throws IOException
   */
  public static Properties loadPropertiesFromClasspath(final String resource) throws IOException {
    final InputResource inputResource = new ClasspathInputResource(resource);
    try (final Reader reader = inputResource.openNewInputReader(UTF_8); ) {
      final Properties properties = new Properties();
      properties.load(reader);
      return properties;
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

  public static String readFileFully(final Path filePath) throws IOException {
    final byte[] bytes = Files.readAllBytes(filePath);
    return new String(bytes, UTF_8);
  }

  public static Path savePropertiesToTempFile(final Properties properties) throws IOException {
    requireNonNull(properties, "No properties provided");
    final Path propertiesFile = Files.createTempFile("schemacrawler", ".properties");
    final Writer writer =
        newBufferedWriter(propertiesFile, UTF_8, WRITE, CREATE, TRUNCATE_EXISTING);
    properties.store(writer, "Temporary file to hold properties");
    return propertiesFile;
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

  static Path buildDirectory() throws Exception {
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

  private TestUtility() {
    // Prevent instantiation
  }
}
