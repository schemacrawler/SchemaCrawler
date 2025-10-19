/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility.extensions;

import java.util.function.Function;
import java.util.regex.Pattern;

final class NeuteredExpressionsFilter implements Function<String, String> {

  private final Pattern[] neuters = {
    // ANSI escape sequences
    Pattern.compile("\u001B\\[[;\\d]*m"),
    Pattern.compile("\u2592"),
    // HSQLDB
    // -- system constraint names
    Pattern.compile("_\\d{5}"),
    // Oracle
    // -- constraint names
    Pattern.compile("SYS_C00\\d{4}"),
    // SQL Server
    // -- primary key names
    Pattern.compile("PK__.{8}__[0-9A-F]{16}"),
    // DuckDB
    // -- JDBC driver configured values
    Pattern.compile("  value                             .*"),
    // Multi-threading
    Pattern.compile("main|pool-\\d+-thread-\\d+"),
  };

  @Override
  public String apply(final String line) {
    String neuteredLine = line;
    for (final Pattern neuter : neuters) {
      neuteredLine = neuter.matcher(neuteredLine).replaceAll("");
    }
    return neuteredLine;
  }
}
