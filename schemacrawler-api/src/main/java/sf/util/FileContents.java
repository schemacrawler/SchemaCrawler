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
package sf.util;


import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.readAllBytes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

public final class FileContents
  implements Supplier<String>
{

  private final Path file;

  public FileContents(final Path file)
  {
    this.file = file;
  }

  @Override
  public String get()
  {
    final String output;
    try
    {
      if (file == null || !isReadable(file) || !isRegularFile(file))
      {
        output = "";
      }
      else
      {
        output = new String(readAllBytes(file));
      }
    }
    catch (final IOException e)
    {
      return "";
    }
    return output;
  }

  @Override
  public String toString()
  {
    return get();
  }

}
