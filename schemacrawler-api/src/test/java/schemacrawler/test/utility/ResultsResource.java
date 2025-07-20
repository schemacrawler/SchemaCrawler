/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static java.nio.charset.StandardCharsets.UTF_8;
import static us.fatehi.utility.IOUtility.isFileReadable;
import static us.fatehi.utility.IOUtility.locateResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;
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
        return locateResource(resourceString) != null;
      case file:
        final Path filePath = Paths.get(resourceString);
        return isFileReadable(filePath);
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
