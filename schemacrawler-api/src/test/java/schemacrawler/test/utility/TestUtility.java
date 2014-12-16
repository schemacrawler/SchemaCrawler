/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.test.utility;


import static java.nio.file.Files.createFile;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.move;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.probeContentType;
import static java.nio.file.Files.size;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sf.util.Utility.UTF8;

import java.awt.image.BufferedImage;
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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.custommonkey.xmlunit.Validator;
import org.xml.sax.SAXException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public final class TestUtility
{

  public static List<String> compareOutput(final String referenceFile,
                                           final Path testOutputTempFile)
    throws Exception
  {
    return compareOutput(referenceFile, testOutputTempFile, "text");
  }

  public static List<String> compareOutput(final String referenceFile,
                                           final Path testOutputTempFile,
                                           final Charset encoding,
                                           final String outputFormat)
    throws Exception
  {

    requireNonNull(referenceFile, "Reference file is not defined");
    requireNonNull(testOutputTempFile, "Output file is not defined");
    requireNonNull(encoding, "Output file encoding is not defined");
    requireNonNull(outputFormat, "Output format is not defined");

    if (testOutputTempFile == null || !exists(testOutputTempFile)
        || !isRegularFile(testOutputTempFile, LinkOption.NOFOLLOW_LINKS)
        || !isReadable(testOutputTempFile) || size(testOutputTempFile) == 0)
    {
      return Collections.singletonList("Output file not created - "
                                       + testOutputTempFile);
    }

    final List<String> failures = new ArrayList<>();

    final boolean contentEquals;
    final Reader referenceReader = readerForResource(referenceFile, encoding);
    if (referenceReader == null)

    {
      contentEquals = false;
    }
    else
    {
      final Reader fileReader = newBufferedReader(testOutputTempFile, encoding);
      contentEquals = contentEquals(referenceReader, fileReader, neuters);
    }

    if ("html".equals(outputFormat))
    {
      validateXHTML(testOutputTempFile, failures);
    }
    else if ("json".equals(outputFormat))
    {
      validateJSON(testOutputTempFile, failures);
    }

    if (!contentEquals)
    {
      final Path testOutputTargetFilePath = get(".",
                                                "unit_tests_results_output",
                                                referenceFile).normalize()
        .toAbsolutePath();
      final boolean isInTarget = testOutputTargetFilePath.toString()
        .contains("target");
      if (!isInTarget)
      {
        throw new RuntimeException("Not in target directory");
      }
      if (!exists(testOutputTargetFilePath))
      {
        createFile(testOutputTargetFilePath);
      }
      delete(testOutputTargetFilePath);
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

  public static List<String> compareOutput(final String referenceFile,
                                           final Path testOutputTempFilePath,
                                           final String outputFormat)
    throws Exception
  {
    return compareOutput(referenceFile,
                         testOutputTempFilePath,
                         UTF8,
                         outputFormat);
  }

  public static Path copyResourceToTempFile(final String resource)
    throws IOException
  {
    try (final InputStream resourceStream = TestUtility.class
      .getResourceAsStream(resource);)
    {
      return writeToTempFile(resourceStream);
    }
  }

  public static Path createTempFile(final String stem,
                                    final String outputFormatValue)
    throws IOException
  {
    final Path testOutputTempFilePath = Files
      .createTempFile(String.format("schemacrawler.%s.", stem),
                      String.format(".%s", outputFormatValue)).normalize()
      .toAbsolutePath();
    delete(testOutputTempFilePath);
    return testOutputTempFilePath;
  }

  public static String currentMethodFullName()
  {
    final StackTraceElement ste = currentMethodStackTraceElement();
    final String className = ste.getClassName();
    return className.substring(className.lastIndexOf('.') + 1) + "."
           + ste.getMethodName();
  }

  public static String currentMethodName()
  {
    final StackTraceElement ste = currentMethodStackTraceElement();
    return ste.getMethodName();
  }

  public static Reader readerForResource(final String resource,
                                         final Charset encoding)
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
        charset = UTF8;
      }
      else
      {
        charset = encoding;
      }
      reader = new InputStreamReader(inputStream, charset);
    }
    else
    {
      reader = null;
    }
    return reader;
  }

  public static void validateDiagram(final Path diagramFile)
    throws IOException
  {
    assertTrue("Diagram file not created", exists(diagramFile));
    assertTrue("Diagram file has 0 bytes size", size(diagramFile) > 0);
    assertEquals("application/png", probeContentType(diagramFile));

    final BufferedImage image = ImageIO.read(diagramFile.toFile());
    assertTrue("Diagram not created", image.getHeight() > 0);
    assertTrue("Diagram not created", image.getWidth() > 0);
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

    try (final BufferedReader expectedBufferedReader = new BufferedReader(expectedInputReader);
        final BufferedReader actualBufferedReader = new BufferedReader(actualInputReader);)
    {
      String expectedline;
      while ((expectedline = expectedBufferedReader.readLine()) != null)
      {
        final String actualLine = actualBufferedReader.readLine();

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
    for (int i = 0; i < stackTrace.length; i++)
    {
      final StackTraceElement stackTraceElement = stackTrace[i];
      if (stackTraceElement.getClassName().equals(TestUtility.class.getName())
          && stackTraceElement.getMethodName()
            .equals("currentMethodStackTraceElement"))
      {
        return stackTrace[i + 2];
      }
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

  private static boolean validateJSON(final Path testOutputFile,
                                      final List<String> failures)
    throws FileNotFoundException, SAXException, IOException
  {
    final JsonElement jsonElement;
    try (final Reader reader = newBufferedReader(testOutputFile, UTF8);
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

  private static boolean validateXHTML(final Path testOutputFile,
                                       final List<String> failures)
    throws Exception
  {
    final DOCTYPEChanger xhtmlReader = new DOCTYPEChanger(newBufferedReader(testOutputFile,
                                                                            UTF8));
    xhtmlReader.setRootElement("html");
    xhtmlReader
      .setSystemIdentifier("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
    xhtmlReader.setPublicIdentifier("-//W3C//DTD XHTML 1.0 Strict//EN");
    xhtmlReader.setReplace(true);

    final boolean isOutputValid;
    try (final Reader reader = new BufferedReader(xhtmlReader);)
    {
      final Validator validator = new Validator(reader);
      isOutputValid = validator.isValid();
      if (!isOutputValid)
      {
        failures.add(validator.toString());
      }
    }
    return isOutputValid;
  }

  private static Path writeToTempFile(final InputStream resourceStream)
    throws IOException, FileNotFoundException
  {
    final Path tempFile = createTempFile("resource", "data").normalize()
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

  private final static Pattern[] neuters = {
      Pattern.compile("url +jdbc:.*"),
      Pattern.compile("database product version.*"),
      Pattern.compile("driver version.*"),
      Pattern.compile(".*[A-Za-z]+ \\d+\\, 2014 \\d+:\\d+ [AP]M.*")
  // Apr 2, 2014 8:38 PM
  };

  private TestUtility()
  {
    // Prevent instantiation
  }

}
