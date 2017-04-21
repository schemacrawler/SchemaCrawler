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
package sf.util;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.isWritable;
import static java.nio.file.Files.size;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.logging.Level;

/**
 * Utility methods.
 *
 * @author Sualeh Fatehi
 */
@UtilityMarker
public final class IOUtility
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(IOUtility.class.getName());

  /**
   * Reads the stream fully, and writes to the writer.
   *
   * @param reader
   *        Reader to read.
   * @return Byte array
   */
  public static void copy(final Reader reader, final Writer writer)
  {
    if (reader == null)
    {
      LOGGER.log(Level.WARNING, "Cannot read null reader");
      return;
    }
    if (writer == null)
    {
      LOGGER.log(Level.WARNING, "Cannot write null writer");
      return;
    }

    final char[] buffer = new char[0x10000];
    try
    {
      // Do not close resources - that is the responsibility of the
      // caller
      final Reader bufferedReader = new BufferedReader(reader, buffer.length);
      final BufferedWriter bufferedWriter = new BufferedWriter(writer,
                                                               buffer.length);

      int read;
      do
      {
        read = bufferedReader.read(buffer, 0, buffer.length);
        if (read > 0)
        {
          bufferedWriter.write(buffer, 0, read);
        }
      } while (read >= 0);

      bufferedWriter.flush();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
  }

  public static Path createTempFilePath(final String stem,
                                        final String extension)
    throws IOException
  {
    final Path tempFilePath = createTempFile("schemacrawler." + stem + ".",
                                             "." + extension).normalize()
                                               .toAbsolutePath();
    delete(tempFilePath);
    tempFilePath.toFile().deleteOnExit();
    return tempFilePath;
  }

  public static String getFileExtension(final Path file)
  {
    if (file == null)
    {
      return "";
    }
    else
    {
      final String fileName = file.toString();
      return getFileExtension(fileName == null? "": fileName);
    }
  }

  public static String getFileExtension(final String fileName)
  {
    final String ext;
    if (fileName != null)
    {
      ext = fileName.lastIndexOf('.') == -1? "": fileName
        .substring(fileName.lastIndexOf('.') + 1,
                   fileName.length());
    }
    else
    {
      ext = "";
    }
    return ext;
  }

  /**
   * Checks if an input file can be read. The file must contain some
   * data.
   *
   * @param file
   *        Input file to read
   * @return True if the file can be read, false otherwise.
   */
  public static boolean isFileReadable(final Path file)
  {
    if (file == null)
    {
      return false;
    }
    if (!isReadable(file) || !isRegularFile(file))
    {
      return false;
    }
    try
    {
      if (size(file) == 0)
      {
        return false;
      }
    }
    catch (final IOException e)
    {
      // Not a critical check, so ignore exception
    }
    return true;
  }

  /**
   * Checks if an output file can be written. The file does not need to
   * exist.
   *
   * @param file
   *        Output file to write
   * @return True if the file can be written, false otherwise.
   */
  public static boolean isFileWritable(final Path file)
  {
    if (file == null)
    {
      return false;
    }
    if (isDirectory(file))
    {
      return false;
    }
    final Path parentPath = file.getParent();
    if (parentPath == null || !exists(parentPath) || !isDirectory(parentPath)
        || !isWritable(parentPath))
    {
      return false;
    }
    return true;
  }

  public static String readFully(final InputStream stream)
  {
    if (stream == null)
    {
      return null;
    }
    final Reader reader = new InputStreamReader(stream, UTF_8);
    return readFully(reader);
  }

  /**
   * Reads the stream fully, and returns a byte array of data.
   *
   * @param reader
   *        Reader to read.
   * @return Byte array
   */
  public static String readFully(final Reader reader)
  {
    if (reader == null)
    {
      LOGGER.log(Level.WARNING, "Cannot read null reader");
      return "";
    }

    try
    {
      final StringWriter writer = new StringWriter();
      copy(reader, writer);
      writer.close();
      return writer.toString();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      return "";
    }

  }

  public static String readResourceFully(final String resource)
  {
    return readFully(IOUtility.class.getResourceAsStream(resource));
  }

  private IOUtility()
  {
    // Prevent instantiation
  }

}
