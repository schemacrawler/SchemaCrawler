/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static us.fatehi.utility.ioresource.InputResourceUtility.wrapReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;

public class ReaderInputResource implements InputResource {

  private static final Logger LOGGER = Logger.getLogger(ReaderInputResource.class.getName());

  private final Reader reader;

  public ReaderInputResource(final Reader reader) {
    this.reader = requireNonNull(reader, "No reader provided");
  }

  @Override
  public Reader openNewInputReader(final Charset charset) {
    LOGGER.log(Level.FINE, "Input to provided reader");
    return wrapReader(getDescription(), new BufferedReader(reader), false);
  }

  @Override
  public String toString() {
    return "<reader>";
  }
}
