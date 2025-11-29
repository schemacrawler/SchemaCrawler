/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility.extensions;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.StringUtils;

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

  private ResultsResource(final String resourceString, final ResourceType resourceType) {
    this.resourceString = resourceString;
    this.resourceType = resourceType;
  }

  public String getResourceString() {
    return resourceString;
  }

  public boolean isAvailable() {
    switch (resourceType) {
      case classpath:
        final String normalized =
            resourceString.startsWith("/") ? resourceString.substring(1) : resourceString;
        try (final InputStream in =
            Thread.currentThread().getContextClassLoader().getResourceAsStream(normalized)) {
          return in != null;
        } catch (final IOException e) {
          return false;
        }

      case file:
        final Path filePath = Path.of(resourceString);
        return Files.exists(filePath)
            && Files.isRegularFile(filePath)
            && Files.isReadable(filePath)
            && getFileSize(filePath) > 0;

      case none:
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
          {
            final String normalized =
                resourceString.startsWith("/") ? resourceString.substring(1) : resourceString;
            final InputStream in =
                Thread.currentThread().getContextClassLoader().getResourceAsStream(normalized);
            if (in != null) {
              return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
            return new BufferedReader(new StringReader(""));
          }
        case file:
          {
            final Path path = Path.of(resourceString);
            return Files.newBufferedReader(path, StandardCharsets.UTF_8);
          }
        case none:
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
    return StringUtils.trimToEmpty(resourceString);
  }

  private long getFileSize(final Path path) {
    try {
      return Files.size(path);
    } catch (final IOException e) {
      return 0;
    }
  }
}
