/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler.exceptions;

public class InternalRuntimeException extends SchemaCrawlerException {

  private static final long serialVersionUID = 3257848770627713076L;

  public InternalRuntimeException(final String message) {
    super(message);
  }

  public InternalRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
