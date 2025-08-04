/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.ioresource;

import static us.fatehi.utility.Utility.isBlank;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

public class InputResourceUtility {

  private static final Logger LOGGER = Logger.getLogger(InputResourceUtility.class.getName());

  /**
   * Creates an input resource from the classpath, or from the file system. If neither are found,
   * returns an empty input resource.
   *
   * @param inputResourceName Name of input resource.
   * @return Input resource
   */
  public static Optional<InputResource> createInputResource(final String inputResourceName) {
    InputResource inputResource = null;
    if (!isBlank(inputResourceName)) {
      try {
        LOGGER.log(Level.FINE, new StringFormat("Attempting to read file <%s>", inputResourceName));
        final Path filePath = Paths.get(inputResourceName);
        inputResource = new FileInputResource(filePath);
      } catch (final Exception e) {
        // No-op
      }
      try {
        if (inputResource == null) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Attempting to read classpath resource <%s>", inputResourceName));
          inputResource = new ClasspathInputResource(inputResourceName);
        }
      } catch (final Exception e) {
        // No-op
      }
      if (inputResource == null) {
        LOGGER.log(
            Level.INFO,
            new StringFormat("Could not locate input resource <%s>", inputResourceName));
      }
    }
    return Optional.ofNullable(inputResource);
  }

  private InputResourceUtility() {
    // Prevent instantiation
  }
}
