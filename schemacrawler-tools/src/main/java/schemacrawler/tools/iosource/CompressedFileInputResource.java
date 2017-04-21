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
package schemacrawler.tools.iosource;


import static java.nio.file.Files.newInputStream;
import static java.util.Objects.requireNonNull;
import static sf.util.IOUtility.isFileReadable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public class CompressedFileInputResource
  implements InputResource
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(CompressedFileInputResource.class.getName());

  private final Path inputFile;
  private final String internalPath;

  public CompressedFileInputResource(final Path filePath,
                                     final String internalPath)
    throws IOException
  {
    inputFile = requireNonNull(filePath, "No file path provided").normalize()
      .toAbsolutePath();
    if (!isFileReadable(inputFile))
    {
      final IOException e = new IOException("Cannot read file, " + inputFile);
      LOGGER.log(Level.CONFIG, e.getMessage(), e);
      throw e;
    }

    this.internalPath = requireNonNull(internalPath,
                                       "No internal file path provided");
  }

  public Path getInputFile()
  {
    return inputFile;
  }

  @Override
  public Reader openNewInputReader(final Charset charset)
    throws IOException
  {
    final InputStream fileStream = newInputStream(inputFile);

    final ZipInputStream zipInputStream = new ZipInputStream(fileStream);
    final ZipEntry zipEntry = zipInputStream.getNextEntry();
    if (zipEntry == null || !zipEntry.getName().equals(internalPath))
    {
      throw new IOException("Zip file does not contain " + internalPath);
    }

    final Reader reader = new InputStreamReader(zipInputStream, charset);
    LOGGER.log(Level.INFO,
               new StringFormat("Opened input reader to compressed file <%s>",
                                inputFile));

    return new InputReader(getDescription(), reader, true);
  }

  @Override
  public String toString()
  {
    return getDescription();
  }

  private String getDescription()
  {
    return inputFile.toString();
  }

}
