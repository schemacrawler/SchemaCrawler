/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schemacrawler.exceptions;

import java.sql.SQLException;

public class DatabaseAccessException extends SchemaCrawlerException {

  private static final long serialVersionUID = 7542373719708607330L;

  public DatabaseAccessException(final String message, final SQLException cause) {
    super(message, cause);
  }

  public DatabaseAccessException(final Throwable cause) {
    super(cause);
  }
}
