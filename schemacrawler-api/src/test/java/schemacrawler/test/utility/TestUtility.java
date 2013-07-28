/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.custommonkey.xmlunit.Validator;
import org.xml.sax.SAXException;

import sf.util.Utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public final class TestUtility
{

  public static List<String> compareOutput(final String referenceFile,
                                           final File testOutputFile)
    throws Exception
  {
    return compareOutput(referenceFile, testOutputFile, null);
  }

  public static List<String> compareOutput(final String referenceFile,
                                           final File testOutputFile,
                                           final Charset encoding,
                                           final String outputFormat)
    throws Exception
  {

    if (testOutputFile == null || !testOutputFile.exists()
        || !testOutputFile.isFile() || !testOutputFile.canRead()
        || testOutputFile.length() == 0)
    {
      return Collections.singletonList("Output file not created - "
                                       + testOutputFile.getAbsolutePath());
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
      final Reader fileReader = readerForFile(testOutputFile, encoding);
      contentEquals = contentEquals(referenceReader,
                                    fileReader,
                                    Pattern.compile("url +jdbc:.*"),
                                    Pattern
                                      .compile("database product version.*"),
                                    Pattern.compile("driver version.*"));
    }

    if ("html".equals(outputFormat))
    {
      validateXHTML(testOutputFile, failures);
    }
    else if ("json".equals(outputFormat))
    {
      validateJSON(testOutputFile, failures);
    }

    if (!contentEquals)
    {
      File testOutputLocalFile = new File("./unit_tests_results_output",
                                          referenceFile);
      if (!testOutputLocalFile.getCanonicalPath().contains("target"))
      {
        final String buildDirectory = System.getProperty("buildDirectory");
        testOutputLocalFile = new File(new File(buildDirectory,
                                                "unit_tests_results_output"),
                                       referenceFile);
      }
      testOutputLocalFile.getParentFile().mkdirs();
      testOutputLocalFile.delete();
      final boolean renamed = testOutputFile.renameTo(testOutputLocalFile);
      if (renamed)
      {
        if (!contentEquals)
        {
          failures.add("Output does not match");
        }

        failures.add("Actual output in "
                     + testOutputLocalFile.getAbsolutePath());
        System.err.println(testOutputLocalFile.getAbsolutePath());
      }
      else
      {
        failures
          .add("Output does not match; could not rename file; see actual output in "
               + testOutputFile.getAbsolutePath());
      }
    }
    else
    {
      testOutputFile.delete();
    }

    return failures;
  }

  public static List<String> compareOutput(final String referenceFile,
                                           final File testOutputFile,
                                           final String outputFormat)
    throws Exception
  {
    return compareOutput(referenceFile,
                         testOutputFile,
                         Charset.defaultCharset(),
                         null);
  }

  public static File copyResourceToTempFile(final String resource)
    throws IOException
  {
    try (final InputStream resourceStream = Utility.class
      .getResourceAsStream(resource);)
    {
      return writeToTempFile(resourceStream);
    }
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

  public static Reader readerForFile(final File file, final Charset encoding)
    throws IOException
  {
    final InputStream inputStream = new FileInputStream(file);
    final Reader reader;
    final Charset charset;
    if (encoding == null)
    {
      charset = Charset.defaultCharset();
    }
    else
    {
      charset = encoding;
    }
    reader = new InputStreamReader(inputStream, charset);
    return reader;
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
        charset = Charset.defaultCharset();
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

  private static boolean validateJSON(final File testOutputFile,
                                      final List<String> failures)
    throws FileNotFoundException, SAXException, IOException
  {
    final JsonElement jsonElement;
    try (final Reader reader = new BufferedReader(new FileReader(testOutputFile));
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

  private static boolean validateXHTML(final File testOutputFile,
                                       final List<String> failures)
    throws Exception
  {
    final DOCTYPEChanger xhtmlReader = new DOCTYPEChanger(new FileReader(testOutputFile));
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

  private static File writeToTempFile(final InputStream resourceStream)
    throws IOException, FileNotFoundException
  {
    final File tempFile = File.createTempFile("SchemaCrawler", ".dat");
    tempFile.deleteOnExit();

    try (final OutputStream tempFileStream = new FileOutputStream(tempFile);)
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
