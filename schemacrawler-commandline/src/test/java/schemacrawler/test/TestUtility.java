/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
package schemacrawler.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.custommonkey.xmlunit.Validator;
import org.junit.Ignore;

import schemacrawler.tools.options.OutputFormat;

@Ignore
public final class TestUtility
{

  public static List<String> compareOutput(final String referenceFile,
                                           final File testOutputFile,
                                           final OutputFormat outputFormat)
    throws Exception
  {

    if (testOutputFile == null || !testOutputFile.exists()
        || !testOutputFile.isFile() || !testOutputFile.canRead()
        || testOutputFile.length() == 0)
    {
      return Collections.singletonList("Output file not created - "
                                       + testOutputFile.getAbsolutePath());
    }

    final List<String> failures = new ArrayList<String>();

    final boolean contentEquals;
    final InputStream referenceStream = TestUtility.class
      .getResourceAsStream("/" + referenceFile);
    if (referenceStream == null)

    {
      contentEquals = false;
    }
    else
    {
      contentEquals = contentEquals(new InputStreamReader(referenceStream),
                                    new FileReader(testOutputFile),
                                    "url                                   jdbc:hsqldb");
    }

    final boolean isOutputValidXml;
    if (outputFormat == OutputFormat.html)
    {
      final Reader reader = new BufferedReader(new FileReader(testOutputFile));
      final Validator validator = new Validator(reader);
      isOutputValidXml = validator.isValid();
      if (!isOutputValidXml)
      {
        failures.add(validator.toString());
      }
      reader.close();
    }
    else
    {
      isOutputValidXml = true;
    }

    if (!contentEquals || !isOutputValidXml)
    {
      final File testOutputLocalFile = new File("./", referenceFile);
      testOutputLocalFile.getParentFile().mkdirs();
      testOutputLocalFile.delete();
      final boolean renamed = testOutputFile.renameTo(testOutputLocalFile);
      if (renamed)
      {
        if (!contentEquals)
        {
          failures.add("Output does not match");
        }
        if (!isOutputValidXml)
        {
          failures.add("Output is invalid XML");
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

    return failures;
  }

  private static boolean contentEquals(final Reader expectedInputReader,
                                       final Reader actualInputReader,
                                       final String... ignoreLines)
    throws Exception
  {
    if (expectedInputReader == null || actualInputReader == null)
    {
      return false;
    }

    final BufferedReader expectedBufferedReader = new BufferedReader(expectedInputReader);
    final BufferedReader actualBufferedReader = new BufferedReader(actualInputReader);
    try
    {
      String expectedline;
      while ((expectedline = expectedBufferedReader.readLine()) != null)
      {
        final String actualLine = actualBufferedReader.readLine();

        boolean ignore = false;
        for (final String ignoreLine: ignoreLines)
        {
          if (expectedline.contains(ignoreLine))
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
    finally
    {
      expectedBufferedReader.close();
      actualBufferedReader.close();
    }
  }

  private TestUtility()
  {
    // Prevent instantiation
  }

}
