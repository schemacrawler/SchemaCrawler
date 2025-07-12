/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.sqlite;

import java.nio.file.Path;

import schemacrawler.tools.options.OutputFormat;

public class SchemaCrawlerSQLiteUtility {

  public static Path executeForOutput(
      final Path dbFile, final String title, final OutputFormat extension) {
    final EmbeddedSQLiteWrapper sqLiteDatabaseLoader = new EmbeddedSQLiteWrapper();
    sqLiteDatabaseLoader.setDatabasePath(dbFile);
    return sqLiteDatabaseLoader.executeForOutput(title, extension);
  }

  private SchemaCrawlerSQLiteUtility() {
    // Prevent instantiation
  }
}
