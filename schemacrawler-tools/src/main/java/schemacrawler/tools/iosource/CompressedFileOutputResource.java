/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
package schemacrawler.tools.iosource;


import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public class CompressedFileOutputResource
  implements OutputResource
{

  private static final Logger LOGGER = Logger
    .getLogger(CompressedFileOutputResource.class.getName());

  private final Path outputFile;

  public CompressedFileOutputResource(final Path filePath)
  {
    outputFile = requireNonNull(filePath, "No file path provided").normalize()
      .toAbsolutePath();
  }

  @Override
  public String getDescription()
  {
    return outputFile.toString();
  }

  public Path getOutputFile()
  {
    return outputFile;
  }

  @Override
  public Writer openOutputWriter(final Charset charset,
                                 final boolean appendOutput)
    throws IOException
  {
    if (appendOutput)
    {
      throw new IOException("Cannot append to compressed file");
    }
    final OpenOption[] openOptions = new OpenOption[] {
        WRITE, CREATE, TRUNCATE_EXISTING
    };
    final OutputStream fileStream = newOutputStream(outputFile, openOptions);
    final Writer writer = new OutputStreamWriter(new GZIPOutputStream(fileStream,
                                                                      true),
                                                 charset);
    LOGGER.log(Level.INFO, "Opened output writer to compressed file, "
                           + outputFile);
    return writer;
  }

  @Override
  public boolean shouldCloseWriter()
  {
    return true;
  }

  @Override
  public String toString()
  {
    return getDescription();
  }

}
