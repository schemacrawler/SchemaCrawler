/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility.ioresource;

import static java.nio.file.Files.newBufferedReader;
import static us.fatehi.utility.IOUtility.isFileReadable;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import us.fatehi.utility.string.StringFormat;

public class FileInputResource implements InputResource {

  private static final Logger LOGGER = Logger.getLogger(FileInputResource.class.getName());

  private final Path inputFile;

  public FileInputResource(final Path filePath) throws IOException {
    inputFile = requireNonNull(filePath, "No file path provided").normalize().toAbsolutePath();
    if (!isFileReadable(inputFile)) {
      final IOException e = new IOException("Cannot read file, " + inputFile);
      LOGGER.log(Level.FINE, e.getMessage(), e);
      throw e;
    }
  }

  public Path getInputFile() {
    return inputFile;
  }

  @Override
  public BufferedReader openNewInputReader(final Charset charset) throws IOException {
    requireNonNull(charset, "No input charset provided");

    final BufferedReader reader = newBufferedReader(inputFile, charset);
    LOGGER.log(Level.FINE, new StringFormat("Opened input reader to file <%s>", inputFile));

    return reader;
  }

  @Override
  public String toString() {
    return inputFile.toString();
  }
}
