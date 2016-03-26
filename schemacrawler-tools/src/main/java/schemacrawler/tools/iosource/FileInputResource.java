/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.file.Files.exists;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.newBufferedReader;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.StringFormat;

public class FileInputResource
  implements InputResource
{

  private static final Logger LOGGER = Logger
    .getLogger(FileInputResource.class.getName());

  private final Path inputFile;

  public FileInputResource(final Path filePath)
    throws IOException
  {
    inputFile = requireNonNull(filePath, "No file path provided").normalize()
      .toAbsolutePath();
    if (!exists(filePath) || !isReadable(filePath))
    {
      throw new IOException("Cannot read file, " + filePath);
    }
  }

  public Path getInputFile()
  {
    return inputFile;
  }

  @Override
  public Reader openNewInputReader(final Charset charset)
    throws IOException
  {
    requireNonNull(charset, "No input charset provided");
    final Reader reader = newBufferedReader(inputFile, charset);
    LOGGER.log(Level.INFO,
               new StringFormat("Opened input reader to file, %s", inputFile));

    return new InputReader(getDescription(), reader, true);
  }

  @Override
  public String toString()
  {
    return getDescription();
  }

  private String getDescription()
  {
    return inputFile.toString();
  }

}
