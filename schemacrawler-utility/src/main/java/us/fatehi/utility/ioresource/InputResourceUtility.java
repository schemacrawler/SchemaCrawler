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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.isBlank;
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
