/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
package sf.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public final class TestUtility
{

  public static void compareOutput(final String referenceFile,
                                   final File testOutputFile,
                                   final List<String> failures)
    throws Exception
  {

    final boolean contentEquals;
    final InputStream referenceStream = TestUtility.class
      .getResourceAsStream("/" + referenceFile);
    if (referenceStream == null)
    {
      contentEquals = false;
    }
    else
    {
      contentEquals = contentEquals(new FileReader(testOutputFile),
                                    new InputStreamReader(referenceStream));
    }
    if (!contentEquals)
    {
      final File testOutputLocalFile = new File("./", referenceFile);
      testOutputLocalFile.getParentFile().mkdirs();
      final boolean renamed = testOutputFile.renameTo(testOutputLocalFile);
      final String message;
      if (renamed)
      {
        message = "Expected file contents in "
                  + testOutputLocalFile.getAbsolutePath();
      }
      else
      {
        message = "Expected file contents in "
                  + testOutputFile.getAbsolutePath();
      }
      failures.add(message);
    }
    if (!testOutputFile.delete())
    {
      failures.add("Cannot delete output file, "
                   + testOutputFile.getAbsolutePath());
    }
  }

  private static boolean contentEquals(final Reader input1, final Reader input2)
    throws Exception
  {
    if (input1 == null || input2 == null)
    {
      return false;
    }

    boolean contentEquals = true;
    final BufferedReader reader1 = new BufferedReader(input1);
    final BufferedReader reader2 = new BufferedReader(input2);
    try
    {
      String line1 = reader1.readLine();
      while (null != line1)
      {
        final String line2 = reader2.readLine();
        if (line2 == null)
        {
          contentEquals = false;
          break;
        }
        if (!line1.trim().equals(line2.trim()))
        {
          contentEquals = false;
          break;
        }
        line1 = reader1.readLine();
      }

      final String line2 = reader2.readLine();
      contentEquals = line2 == null;
    }
    finally
    {
      reader1.close();
      reader2.close();
    }
    return contentEquals;
  }

  private TestUtility()
  {
    // Prevent instantiation
  }

}
