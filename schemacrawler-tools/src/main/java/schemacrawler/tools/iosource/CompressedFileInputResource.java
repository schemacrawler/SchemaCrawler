/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * within even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.iosource;


import static java.nio.file.Files.exists;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.newInputStream;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public class CompressedFileInputResource
  implements InputResource
{

  private static final Logger LOGGER = Logger
    .getLogger(CompressedFileInputResource.class.getName());

  private final Path inputFile;

  public CompressedFileInputResource(final Path filePath)
    throws IOException
  {
    inputFile = requireNonNull(filePath, "No file path provided").normalize()
      .toAbsolutePath();
    if (!exists(filePath) || !isReadable(filePath))
    {
      throw new IOException("Cannot read file, " + filePath);
    }
  }

  @Override
  public String getDescription()
  {
    return inputFile.toString();
  }

  public Path getInputFile()
  {
    return inputFile;
  }

  @Override
  public Reader openInputReader(final Charset charset)
    throws IOException
  {
    final InputStream fileStream = newInputStream(inputFile);
    final Reader reader = new InputStreamReader(new GZIPInputStream(fileStream),
                                                charset);
    LOGGER.log(Level.INFO, "Opened input reader to compressed file, "
                           + inputFile);
    return reader;
  }

  @Override
  public boolean shouldCloseReader()
  {
    return true;
  }

  @Override
  public String toString()
  {
    return getDescription();
  }

}
