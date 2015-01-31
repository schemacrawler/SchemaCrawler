/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.options;


import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClasspathInputResource
  implements InputResource
{

  private static final Logger LOGGER = Logger
    .getLogger(ClasspathInputResource.class.getName());

  private final String classpathResource;

  public ClasspathInputResource(final String classpathResource)
    throws IOException
  {
    this.classpathResource = requireNonNull(classpathResource,
                                            "No classpath resource provided");
    if (ClasspathInputResource.class.getResource(this.classpathResource) == null)
    {
      throw new IOException("Cannot read classpath resource, "
                            + this.classpathResource);
    }
  }

  @Override
  public String getDescription()
  {
    return InputReader.class.getResource(classpathResource).toExternalForm();
  }

  @Override
  public Reader openInputReader(final Charset charset)
    throws IOException
  {
    requireNonNull(charset, "No input charset provided");
    final InputStream inputStream = ClasspathInputResource.class
      .getResourceAsStream(classpathResource);
    final Reader reader = new BufferedReader(new InputStreamReader(inputStream,
                                                                   charset));
    LOGGER.log(Level.INFO, "Opened input reader to classpath resource, "
                           + classpathResource);
    return reader;
  }

  @Override
  public boolean shouldCloseReader()
  {
    return true;
  }

  @Override
  public String toString()
  {
    return getDescription();
  }

}
