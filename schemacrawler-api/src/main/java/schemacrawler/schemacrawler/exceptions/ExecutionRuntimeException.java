/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler.exceptions;

public class ExecutionRuntimeException extends SchemaCrawlerException {

  private static final long serialVersionUID = 8143604098031489051L;

  public ExecutionRuntimeException(final String message) {
    super(message);
  }

  public ExecutionRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ExecutionRuntimeException(final Throwable cause) {
    super(cause);
  }
}
