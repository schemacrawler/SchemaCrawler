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


import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;
import static sf.util.IOUtility.isFileWritable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public class CompressedFileOutputResource
  implements OutputResource
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(CompressedFileOutputResource.class.getName());

  private final Path outputFile;
  private final String internalPath;

  public CompressedFileOutputResource(final Path outputFile,
                                      final String internalPath)
    throws IOException
  {
    requireNonNull(outputFile, "No output file provided");

    this.outputFile = outputFile.normalize().toAbsolutePath();
    if (!isFileWritable(this.outputFile))
    {
      throw new IOException("Cannot write output file, " + this.outputFile);
    }

    this.internalPath = requireNonNull(internalPath,
                                       "No internal file path provided");
  }

  public Path getOutputFile()
  {
    return outputFile;
  }

  @Override
  public Writer openNewOutputWriter(final Charset charset,
                                    final boolean appendOutput)
    throws IOException
  {
    if (appendOutput)
    {
      throw new IOException("Cannot append to compressed file");
    }
    final OpenOption[] openOptions = new OpenOption[] {
                                                        WRITE,
                                                        CREATE,
                                                        TRUNCATE_EXISTING };
    final OutputStream fileStream = newOutputStream(outputFile, openOptions);

    final ZipOutputStream zipOutputStream = new ZipOutputStream(fileStream);
    zipOutputStream.putNextEntry(new ZipEntry(internalPath));

    final Writer writer = new OutputStreamWriter(zipOutputStream, charset);
    LOGGER.log(Level.INFO,
               new StringFormat("Opened output writer to compressed file <%s>",
                                outputFile));
    return new OutputWriter(getDescription(), writer, true);
  }

  @Override
  public String toString()
  {
    return getDescription();
  }

  private String getDescription()
  {
    return outputFile.toString();
  }

}
