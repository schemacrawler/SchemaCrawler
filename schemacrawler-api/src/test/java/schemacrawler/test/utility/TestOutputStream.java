/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.utility;

import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import us.fatehi.utility.IOUtility;

public final class TestOutputStream extends OutputStream implements TestOutputCapture {

  private final Path tempFile;
  private final OutputStream out;

  public TestOutputStream() {
    try {
      tempFile = IOUtility.createTempFilePath("test", "");
      out = newOutputStream(tempFile, WRITE, CREATE, TRUNCATE_EXISTING);
    } catch (final IOException e) {
      throw new RuntimeException("Could not open output stream to a temporary file", e);
    }
  }

  @Override
  public String getContents() {
    try {
      out.flush();
      out.close();
      return new String(readAllBytes(tempFile), StandardCharsets.UTF_8);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Path getFilePath() {
    return tempFile;
  }

  @Override
  public String toString() {
    return tempFile.toString();
  }

  @Override
  public void write(final int b) throws IOException {
    out.write(b);
  }
}
