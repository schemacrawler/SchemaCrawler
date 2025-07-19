/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

public class SQLRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 7185042951359266632L;

  public SQLRuntimeException(final String message) {
    super(message);
  }

  public SQLRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public SQLRuntimeException(final Throwable cause) {
    super(cause);
  }
}
