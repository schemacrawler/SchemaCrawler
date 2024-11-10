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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;

/**
 * Converts a provided classpath resource into an input resource. NOTE: Always assumes that
 * resources are absolute. Leading slashes are not required, but ignored if provided.
 */
public class ClasspathInputResource implements InputResource {

  private static final Logger LOGGER = Logger.getLogger(ClasspathInputResource.class.getName());

  private final String classpathResource;
  private URL url;

  public ClasspathInputResource(final String classpathResource) throws IOException {
    this.classpathResource = requireNonNull(classpathResource, "No classpath resource provided");
    url = locateResource();
    if (url == null) {
      final IOException e = new IOException("Cannot read classpath resource, " + classpathResource);
      LOGGER.log(Level.FINE, e.getMessage(), e);
      throw e;
    }
  }

  public String getClasspathResource() {
    return classpathResource;
  }

  @Override
  public String getDescription() {
    return url.toExternalForm();
  }

  @Override
  public BufferedReader openNewInputReader(final Charset charset) {
    requireNonNull(charset, "No input charset provided");
    try {
      final InputStream inputStream = url.openStream();
      final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
      LOGGER.log(Level.FINE, "Opened input reader to classpath resource, " + classpathResource);

      return reader;
    } catch (final IOException e) {
      LOGGER.log(Level.FINE, "Could not read classpath resource", e);
      throw new NullPointerException(
          String.format("Cannot open classpath resource <%s> for reading", classpathResource));
    }
  }

  @Override
  public String toString() {
    return classpathResource;
  }

  /**
   * Locates the resource bases on the current thread's classloader. Always assumes that resources
   * are absolute.
   *
   * @return URL for the located resource, or null if not found
   */
  private URL locateResource() {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    final String resolvedClasspathResource;
    if (classpathResource.startsWith("/")) {
      resolvedClasspathResource = classpathResource.substring(1);
    } else {
      resolvedClasspathResource = classpathResource;
    }
    return classLoader.getResource(resolvedClasspathResource);
  }
}
