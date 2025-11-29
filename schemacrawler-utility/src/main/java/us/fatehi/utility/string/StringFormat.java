/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.string;

import java.util.Formatter;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.Utility;

public final class StringFormat implements Supplier<String> {

  private static final Logger LOGGER = Logger.getLogger(StringFormat.class.getName());

  private final Object[] args;
  private final String format;

  public StringFormat(final String format, final Object... args) {
    // Be tolerant - allow null or blank format strings
    this.format = format;
    this.args = args;
  }

  @Override
  public String get() {
    if (Utility.isBlank(format) || args == null || args.length == 0) {
      return format;
    }

    try (final Formatter formatter = new Formatter()) {
      return formatter.format(format, args).toString();
    } catch (final Throwable e) {
      // NOTE: Do not output arguments, since the toString on argument may throw an exception
      // obscuring this one
      LOGGER.log(Level.FINEST, "Error logging message <%s>".formatted(format));
      return "";
    }
  }

  @Override
  public String toString() {
    return get();
  }
}
