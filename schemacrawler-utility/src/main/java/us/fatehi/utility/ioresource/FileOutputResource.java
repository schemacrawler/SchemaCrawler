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

import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.ioresource.InputResourceUtility.wrapWriter;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import us.fatehi.utility.string.StringFormat;

public final class FileOutputResource implements OutputResource {

  private static final Logger LOGGER = Logger.getLogger(FileOutputResource.class.getName());

  private final Path outputFile;

  public FileOutputResource(final Path filePath) {
    outputFile = requireNonNull(filePath, "No file path provided").normalize().toAbsolutePath();
  }

  public Path getOutputFile() {
    return outputFile;
  }

  @Override
  public Writer openNewOutputWriter(final Charset charset, final boolean appendOutput)
      throws IOException {
    requireNonNull(charset, "No output charset provided");
    final OpenOption[] openOptions;
    if (appendOutput) {
      openOptions = new OpenOption[] {WRITE, CREATE, APPEND};
    } else {
      openOptions = new OpenOption[] {WRITE, CREATE, TRUNCATE_EXISTING};
    }
    final Writer writer = newBufferedWriter(outputFile, charset, openOptions);
    LOGGER.log(Level.FINE, new StringFormat("Opened output writer to file <%s>", outputFile));
    return wrapWriter(getDescription(), writer, true);
  }

  @Override
  public String toString() {
    return outputFile.toString();
  }
}
