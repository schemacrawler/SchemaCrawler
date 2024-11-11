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

package schemacrawler.test.utility;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.size;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.ioresource.ClasspathInputResource;
import us.fatehi.utility.ioresource.FileInputResource;

public final class ResultsResource {

  public enum ResourceType {
    none,
    classpath,
    file;
  }

  public static ResultsResource fromClasspath(final String resource) {
    final String resourceString;
    if (!isBlank(resource)) {
      resourceString = resource;
    } else {
      resourceString = null;
    }

    return new ResultsResource(resourceString, ResourceType.classpath);
  }

  /**
   * Records the expected file path. The output file may or may not exist at the time this is
   * created.
   *
   * @param filePath Expected path
   * @return Test resource
   */
  public static ResultsResource fromFilePath(final Path filePath) {
    final String resourceString;
    if (filePath != null) {
      resourceString = filePath.toString();
    } else {
      resourceString = null;
    }

    return new ResultsResource(resourceString, ResourceType.file);
  }

  public static ResultsResource none() {
    return new ResultsResource(null, ResourceType.none);
  }

  private final String resourceString;
  private final ResourceType resourceType;

  private ResultsResource(String resourceString, final ResourceType resourceType) {
    this.resourceString = resourceString;
    this.resourceType = resourceType;
  }

  public String getResourceString() {
    return resourceString;
  }

  public boolean isAvailable() {
    switch (resourceType) {
      case classpath:
        return IOUtility.locateResource(resourceString) != null;
      case file:
        final Path filePath = Paths.get(resourceString);
        try {
          return IOUtility.isFileReadable(filePath) && size(filePath) > 0;
        } catch (final IOException e) {
          return false;
        }
      case none:
      // Fall-through
      default:
        return false;
    }
  }

  public boolean isNone() {
    return resourceType == ResourceType.none;
  }

  public BufferedReader openNewReader() {
    try {
      switch (resourceType) {
        case classpath:
          return new ClasspathInputResource(resourceString).openNewInputReader(UTF_8);
        case file:
          return new FileInputResource(Paths.get(resourceString)).openNewInputReader(UTF_8);
        case none:
        // Fall-through
        default:
          return new BufferedReader(new StringReader(""));
      }
    } catch (final IOException e) {
      return new BufferedReader(new StringReader(""));
    }
  }

  @Override
  public String toString() {
    if (isNone()) {
      return "<none>";
    }
    return trimToEmpty(resourceString);
  }
}
