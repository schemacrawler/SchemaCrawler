/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test.utility;

import java.util.function.Predicate;
import java.util.regex.Pattern;

final class SvgElementFilter implements Predicate<String> {

  private final Pattern start = Pattern.compile("<svg.*");
  private final Pattern end = Pattern.compile(".*svg>");
  private boolean isFiltering;

  @Override
  public boolean test(final String line) {
    if (!isFiltering && start.matcher(line).matches()) {
      isFiltering = true;
      // Filter out the start SVG tag
      return false;
    } else if (isFiltering && end.matcher(line).matches()) {
      isFiltering = false;
      // Filter out the end SVG tag
      return false;
    }

    return !isFiltering;
  }
}
