/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.ioresource;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.IOUtility.locateResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Converts a provided classpath resource into an input resource. NOTE: Always assumes that
 * resources are absolute. Leading slashes are not required, but ignored if provided.
 */
public class ClasspathInputResource extends BaseInputResource {

  private final URL url;

  public ClasspathInputResource(final String classpathResource) throws IOException {
    requireNonNull(classpathResource, "No classpath resource provided");
    url = locateResource(classpathResource);
    if (url == null) {
      final IOException e =
          new IOException("Cannot read classpath resource, <%s>".formatted(classpathResource));
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
