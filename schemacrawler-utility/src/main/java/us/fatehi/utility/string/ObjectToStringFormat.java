/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.string;

import static us.fatehi.utility.Utility.isBlank;

import java.util.function.Supplier;
import us.fatehi.utility.ObjectToString;

public final class ObjectToStringFormat implements Supplier<String> {

  private final String context;
  private final Object args;

  public ObjectToStringFormat(final Object args) {
    this(null, args);
  }

  public ObjectToStringFormat(final String context, final Object args) {
    this.context = context;
    this.args = args;
  }

  @Override
  public String get() {
    final StringBuilder buffer = new StringBuilder();
    if (!isBlank(context)) {
      buffer.append(context).append(System.lineSeparator());
    }
    if (args != null) {
      buffer.append(ObjectToString.toString(args));
    }
    return buffer.toString();
  }

  @Override
  public String toString() {
    return get();
  }
}
