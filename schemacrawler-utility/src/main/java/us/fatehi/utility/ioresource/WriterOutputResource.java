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
package us.fatehi.utility.ioresource;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.ioresource.InputResourceUtility.wrapWriter;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class WriterOutputResource implements OutputResource {

  private static final Logger LOGGER = Logger.getLogger(WriterOutputResource.class.getName());

  private final Writer writer;

  public WriterOutputResource(final Writer writer) {
    this.writer = requireNonNull(writer, "No writer provided");
  }

  @Override
  public Writer openNewOutputWriter(final Charset charset, final boolean appendOutput) {
    LOGGER.log(Level.FINE, "Output to provided writer");
    return wrapWriter(getDescription(), writer, false);
  }

  @Override
  public String toString() {
    return "<writer>";
  }
}
