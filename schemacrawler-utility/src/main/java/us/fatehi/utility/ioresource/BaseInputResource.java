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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import static java.util.Objects.requireNonNull;

abstract class BaseInputResource implements InputResource {

  private static final Logger LOGGER = Logger.getLogger(BaseInputResource.class.getName());

  protected abstract InputStream openNewInputStream() throws IOException;

  @Override
  public final BufferedReader openNewInputReader(final Charset charset) throws IOException {
    return openNewInputReader(charset, false);
  }

  @Override
  public final BufferedReader openNewCompressedInputReader(final Charset charset)
      throws IOException {
    return openNewInputReader(charset, true);
  }

  private BufferedReader openNewInputReader(final Charset charset, final boolean isCompressed)
      throws IOException {
    requireNonNull(charset, "No input charset provided");

    final InputStream inputStream;
    if (isCompressed) {
      inputStream = openNewInputStream();
      requireNonNull(charset, "No input stream provided");
    } else {
      inputStream = openNewCompressedInputStream(openNewInputStream());
    }

    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
    LOGGER.log(Level.FINE, String.format("Opened resource <%s> for reading", getDescription()));

    return reader;
  }

  private static InputStream openNewCompressedInputStream(final InputStream inputStream)
      throws IOException {
    final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
    zipInputStream.getNextEntry();
    return zipInputStream;
  }
}
