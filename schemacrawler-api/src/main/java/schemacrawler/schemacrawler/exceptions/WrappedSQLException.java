/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schemacrawler.exceptions;

import static schemacrawler.utility.ExceptionUtility.makeExceptionMessage;

import java.sql.SQLException;

public class WrappedSQLException extends SQLException {

  private static final long serialVersionUID = 3424948223257267142L;

  public WrappedSQLException(final String message, final SQLException cause) {
    super(
        makeExceptionMessage(message, cause),
        cause == null ? "" : cause.getSQLState(),
        cause == null ? 0 : cause.getErrorCode(),
        cause);
  }
}
