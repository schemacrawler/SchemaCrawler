/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.size;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertTrue;
import static sf.util.IOUtility.isFileReadable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import sf.util.IOUtility;

public final class TestUtility
{

  private final static Pattern[] neuters = {
                                             Pattern.compile("url +jdbc:.*"),
                                             Pattern
                                               .compile("database product version.*"),
                                             Pattern
                                               .compile("driver version.*"),
                                             Pattern
                                               .compile("\\s+<schemaCrawlerVersion>.*"),
                                             Pattern
                                               .compile("\\s+<schemaCrawlerAbout>.*"),
                                             Pattern
                                               .compile("\\s+<schemaCrawlerInfo>.*"),
                                             Pattern
                                               .compile(".*[A-Za-z]+ \\d+\\, 201[456] \\d+:\\d+ [AP]M.*"),
                                             Pattern
                                               .compile(".*201[4-7]-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d.*"),
                                             Pattern
                                               .compile(".*201[4-7]-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d.*"),
                                             // SVG {
                                             Pattern.compile("<svg.*"),
                                             Pattern
                                               .compile(" viewBox=\"0.00.*"),
                                             Pattern.compile("<g id=.*"),
                                             Pattern
                                               .compile("<text text-anchor.*"),
                                             Pattern.compile("<path fill=.*"),
                                             Pattern
                                               .compile("<ellipse fill=.*"),
                                             Pattern
                                               .compile("<polyline fill=.*"),
                                             Pattern
                                               .compile("<polygon fill=.*"),
                                             Pattern.compile(".*&#xd;"), };

  public static void clean(final String dirname)
    throws Exception
  {
    FileUtils.deleteDirectory(buildDirectory()
      .resolve("unit_tests_results_output").resolve(dirname).toFile());
  }

  public static List<String> compareCompressedOutput(final String referenceFile,
                                                     final Path testOutputTempFile,
                                                     final String outputFormat)
    throws Exception
  {
    return compareOutput(referenceFile, testOutputTempFile, outputFormat, true);
  }

  public static List<String> compareOutput(final String referenceFile,
                                           final Path testOutputTempFile,
                                           final String outputFormat)
    throws Exception
  {
    return compareOutput(referenceFile,
                         testOutputTempFile,
                         outputFormat,
                         false);
  }

  public static List<String> compareOutput(final String referenceFile,
                                           final Path testOutputTempFile,
                                           final String outputFormat,
                                           final boolean isCompressed)
    throws Exception
  {

    requireNonNull(referenceFile, "Reference file is not defined");
    requireNonNull(testOutputTempFile, "Output file is not defined");
    requireNonNull(outputFormat, "Output format is not defined");

    if (!isFileReadable(testOutputTempFile))
    {
      return Collections
        .singletonList("Output file not created - " + testOutputTempFile);
    }

    final List<String> failures = new ArrayList<>();

    final boolean contentEquals;
    final Reader referenceReader = readerForResource(referenceFile,
                                                     UTF_8,
                                                     isCompressed);
    if (referenceReader == null)

    {
      contentEquals = false;
    }
    else
    {
      final Reader fileReader = readerForFile(testOutputTempFile, isCompressed);
      contentEquals = contentEquals(referenceReader, fileReader, neuters);
    }

    if ("html".equals(outputFormat))
    {
      validateXML(testOutputTempFile, failures);
    }
    if ("htmlx".equals(outputFormat))
    {
      validateXML(testOutputTempFile, failures);
    }
    else if ("json".equals(outputFormat))
    {
      validateJSON(testOutputTempFile, failures);
    }

    if (!contentEquals)
    {
      final Path testOutputTargetFilePath = buildDirectory()
        .resolve("unit_tests_results_output").resolve(referenceFile);
      createDirectories(testOutputTargetFilePath.getParent());
      deleteIfExists(testOutputTargetFilePath);
      move(testOutputTempFile,
           testOutputTargetFilePath,
           ATOMIC_MOVE,
           REPLACE_EXISTING);

      if (!contentEquals)
      {
        failures.add("Output does not match");
      }

      failures.add("Actual output in " + testOutputTargetFilePath);
      System.err.println(testOutputTargetFilePath);
    }
    else
    {
      delete(testOutputTempFile);
    }

    return failures;
  }

  public static Path copyResourceToTempFile(final String resource)
    throws IOException
  {
    try (final InputStream resourceStream = TestUtility.class
      .getResourceAsStream(resource);)
    {
      requireNonNull(resourceStream, "Resource not found, " + resource);
      return writeToTempFile(resourceStream);
    }
  }

  public static String[] flattenCommandlineArgs(final Map<String, String> argsMap)
  {
    final List<String> argsList = new ArrayList<>();
    for (final Map.Entry<String, String> arg: argsMap.entrySet())
    {
      final String key = arg.getKey();
      final String value = arg.getValue();
      if (value != null)
      {
        argsList.add(String.format("-%s=%s", key, value));
      }
      else
      {
        argsList.add(String.format("-%s", key));
      }
    }
    final String[] args = argsList.toArray(new String[0]);
    return args;
  }

  public static Reader readerForResource(final String resource,
                                         final Charset encoding)
    throws IOException
  {
    return readerForResource(resource, encoding, false);
  }

  public static void validateDiagram(final Path diagramFile)
    throws IOException
  {
    assertTrue("Diagram file not created", exists(diagramFile));
    assertTrue("Diagram file has 0 bytes size", size(diagramFile) > 0);
  }

  private static Path buildDirectory()
    throws Exception
  {
    final StackTraceElement ste = currentMethodStackTraceElement();
    final Class<?> callingClass = Class.forName(ste.getClassName());
    final Path codePath = Paths.get(callingClass.getProtectionDomain()
      .getCodeSource().getLocation().toURI()).normalize().toAbsolutePath();
    final boolean isInTarget = codePath.toString().contains("target");
    if (!isInTarget)
    {
      throw new RuntimeException("Not in build directory, " + codePath);
    }
    final Path directory = codePath.resolve("..");
    return directory.normalize().toAbsolutePath();
  }

  private static boolean contentEquals(final Reader expectedInputReader,
                                       final Reader actualInputReader,
                                       final Pattern... ignoreLinePatterns)
    throws Exception
  {
    if (expectedInputReader == null || actualInputReader == null)
    {
      return false;
    }

    int i = 0;
    try (
        final BufferedReader expectedBufferedReader = new BufferedReader(expectedInputReader);
        final BufferedReader actualBufferedReader = new BufferedReader(actualInputReader);)
    {
      String expectedline;
      while ((expectedline = expectedBufferedReader.readLine()) != null)
      {
        final String actualLine = actualBufferedReader.readLine();
        i++;
        boolean ignore = false;
        for (final Pattern ignoreLinePattern: ignoreLinePatterns)
        {
          if (ignoreLinePattern.matcher(expectedline).matches())
          {
            ignore = true;
            break;
          }
        }
        if (ignore)
        {
          continue;
        }

        if (!expectedline.equals(actualLine))
        {
          System.out.println("Line #" + i + " (expected followed by actual)");
          System.out.println(expectedline);
          System.out.println(actualLine);
          return false;
        }
      }

      if (actualBufferedReader.readLine() != null)
      {
        return false;
      }
      if (expectedBufferedReader.readLine() != null)
      {
        return false;
      }

      return true;
    }
  }

  private static StackTraceElement currentMethodStackTraceElement()
  {
    final StackTraceElement[] stackTrace = Thread.currentThread()
      .getStackTrace();
    final Iterator<StackTraceElement> stackTraceIterator = Arrays
      .asList(stackTrace).iterator();
    int i = 0;
    while (stackTraceIterator.hasNext())
    {
      final StackTraceElement stackTraceElement = stackTraceIterator.next();
      if (stackTraceElement.getClassName().equals(TestUtility.class.getName()))
      {
        while (stackTraceIterator.hasNext())
        {
          final String className = stackTraceIterator.next().getClassName();
          if (!className.equals(TestUtility.class.getName())
              && !className.equals(TestWriter.class.getName())
              && !className.matches(".*\\.Base.*Test"))
          {
            return stackTrace[i + 1];
          }
          i++;
        }
      }
      i++;
    }

    return null;
  }

  private static void fastChannelCopy(final ReadableByteChannel src,
                                      final WritableByteChannel dest)
    throws IOException
  {
    final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
    while (src.read(buffer) != -1)
    {
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
    while (buffer.hasRemaining())
    {
      dest.write(buffer);
    }
  }

  private static Reader openNewCompressedInputReader(final InputStream inputStream,
                                                     final Charset charset)
    throws IOException
  {
    final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
    zipInputStream.getNextEntry();
    return new InputStreamReader(zipInputStream, charset);
  }

  private static Reader readerForFile(final Path testOutputTempFile)
    throws IOException
  {
    return readerForFile(testOutputTempFile, false);
  }

  private static Reader readerForFile(final Path testOutputTempFile,
                                      final boolean isCompressed)
    throws IOException
  {

    final BufferedReader bufferedReader;
    if (isCompressed)
    {
      final ZipInputStream inputStream = new ZipInputStream(newInputStream(testOutputTempFile,
                                                                           StandardOpenOption.READ));
      inputStream.getNextEntry();

      bufferedReader = new BufferedReader(new InputStreamReader(inputStream,
                                                                UTF_8));
    }
    else
    {
      bufferedReader = newBufferedReader(testOutputTempFile, UTF_8);
    }
    return bufferedReader;
  }

  private static Reader readerForResource(final String resource,
                                          final Charset encoding,
                                          final boolean isCompressed)
    throws IOException
  {
    final InputStream inputStream = TestUtility.class
      .getResourceAsStream("/" + resource);
    final Reader reader;
    if (inputStream != null)
    {
      final Charset charset;
      if (encoding == null)
      {
        charset = UTF_8;
      }
      else
      {
        charset = encoding;
      }
      if (isCompressed)
      {
        reader = openNewCompressedInputReader(inputStream, charset);
      }
      else
      {
        reader = new InputStreamReader(inputStream, charset);
      }
    }
    else
    {
      reader = null;
    }
    return reader;
  }

  private static boolean validateJSON(final Path testOutputFile,
                                      final List<String> failures)
    throws FileNotFoundException, SAXException, IOException
  {
    final JsonElement jsonElement;
    try (final Reader reader = readerForFile(testOutputFile);
        final JsonReader jsonReader = new JsonReader(reader);)
    {
      jsonElement = new JsonParser().parse(jsonReader);
      if (jsonReader.peek() != JsonToken.END_DOCUMENT)
      {
        failures.add("JSON document was not fully consumed.");
      }
    }
    catch (final Exception e)
    {
      failures.add(e.getMessage());
      return false;
    }

    final int size;
    if (jsonElement.isJsonObject())
    {
      size = jsonElement.getAsJsonObject().entrySet().size();
    }
    else if (jsonElement.isJsonArray())
    {
      size = jsonElement.getAsJsonArray().size();
    }
    else
    {
      size = 0;
    }

    if (size == 0)
    {
      failures.add("Invalid JSON string");
    }

    return failures.isEmpty();
  }

  private static void validateXML(final Path testOutputFile,
                                  final List<String> failures)
    throws Exception
  {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);

    final DocumentBuilder builder = factory.newDocumentBuilder();
    builder.setErrorHandler(new ErrorHandler()
    {
      @Override
      public void error(final SAXParseException e)
        throws SAXException
      {
        failures.add(e.getMessage());
      }

      @Override
      public void fatalError(final SAXParseException e)
        throws SAXException
      {
        failures.add(e.getMessage());
      }

      @Override
      public void warning(final SAXParseException e)
        throws SAXException
      {
        failures.add(e.getMessage());
      }
    });
    builder.parse(new InputSource(readerForFile(testOutputFile)));
  }

  private static Path writeToTempFile(final InputStream resourceStream)
    throws IOException, FileNotFoundException
  {
    final Path tempFile = IOUtility.createTempFilePath("resource", "data").normalize()
      .toAbsolutePath();

    try (final OutputStream tempFileStream = newOutputStream(tempFile,
                                                             WRITE,
                                                             TRUNCATE_EXISTING,
                                                             CREATE);)
    {
      fastChannelCopy(Channels.newChannel(resourceStream),
                      Channels.newChannel(tempFileStream));
    }

    return tempFile;
  }

  private TestUtility()
  {
    // Prevent instantiation
  }

}
