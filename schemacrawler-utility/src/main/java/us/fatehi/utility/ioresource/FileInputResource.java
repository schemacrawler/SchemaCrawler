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

import static java.nio.file.Files.newInputStream;
import static us.fatehi.utility.IOUtility.isFileReadable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import static java.util.Objects.requireNonNull;

public class FileInputResource extends BaseInputResource {

  private final Path inputFile;

  public FileInputResource(final Path filePath) throws IOException {
    inputFile = requireNonNull(filePath, "No file path provided").normalize().toAbsolutePath();
    if (!isFileReadable(inputFile)) {
      final IOException e = new IOException(String.format("Cannot read file, <%s>", inputFile));
      throw e;
    }
  }

  @Override
  public InputStream openNewInputStream() throws IOException {
    final InputStream reader = newInputStream(inputFile);
    return reader;
  }

  @Override
  public String toString() {
    return inputFile.toString();
  }
}
