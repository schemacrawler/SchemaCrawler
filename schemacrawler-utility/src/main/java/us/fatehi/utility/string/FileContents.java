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

package us.fatehi.utility.string;

import static java.nio.file.Files.readAllBytes;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.function.Supplier;

public final class FileContents implements Supplier<String> {

  private final Charset charset;
  private final Path file;

  public FileContents(final Path file) {
    this(file, Charset.defaultCharset());
  }

  public FileContents(final Path file, final Charset charset) {
    this.file = requireNonNull(file, "No file path provided");
    this.charset = requireNonNull(charset, "No charset provided");
  }

  @Override
  public String get() {
    final String output;
    try {
      output = new String(readAllBytes(file), charset);
    } catch (final IOException e) {
      return "";
    }
    return output;
  }

  @Override
  public String toString() {
    return get();
  }
}
