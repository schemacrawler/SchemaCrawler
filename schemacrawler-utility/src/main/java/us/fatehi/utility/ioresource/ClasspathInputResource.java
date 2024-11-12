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

import static us.fatehi.utility.IOUtility.locateResource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import static java.util.Objects.requireNonNull;

/**
 * Converts a provided classpath resource into an input resource. NOTE: Always assumes that
 * resources are absolute. Leading slashes are not required, but ignored if provided.
 */
public class ClasspathInputResource extends BaseInputResource {

  private URL url;

  public ClasspathInputResource(final String classpathResource) throws IOException {
    requireNonNull(classpathResource, "No classpath resource provided");
    url = locateResource(classpathResource);
    if (url == null) {
      final IOException e =
          new IOException(String.format("Cannot read classpath resource, <%s>", classpathResource));
      throw e;
    }
  }

  @Override
  public InputStream openNewInputStream() throws IOException {
    final InputStream inputStream = url.openStream();
    return inputStream;
  }

  @Override
  public String toString() {
    return url.toExternalForm();
  }
}
