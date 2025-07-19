/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.property;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.isBlank;

public abstract class PropertyNameUtility {

  private static final Logger LOGGER = Logger.getLogger(PropertyNameUtility.class.getName());

  public static String tableOf(final String title, final Collection<PropertyName> propertyNames) {
    int index = 0;
    final StringBuilder buffer = new StringBuilder(1024);
    try {
      int maxNameLength = 0;
      for (final PropertyName registeredPlugin : propertyNames) {
        final int length = registeredPlugin.getName().length();
        if (length > maxNameLength) {
          maxNameLength = length;
        }
      }
      final String format = String.format("%%2d %%%ds %%s", maxNameLength);

      if (!isBlank(title)) {
        buffer.append(title).append(System.lineSeparator());
      }
      for (final PropertyName registeredPlugin : propertyNames) {
        index++;
        buffer
            .append(
                String.format(
                    format, index, registeredPlugin.getName(), registeredPlugin.getDescription()))
            .append(System.lineSeparator());
      }
    } catch (final Throwable e) {
      // Log the error and continue
      LOGGER.log(Level.INFO, "Could not list " + title, e);
    }
    return buffer.toString();
  }

  private PropertyNameUtility() {
    // Prevent instantiation
  }
}
