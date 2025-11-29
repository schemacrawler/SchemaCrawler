/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler.exceptions;

import static schemacrawler.utility.ExceptionUtility.makeExceptionMessage;

import java.io.Serial;

public class SchemaCrawlerException extends RuntimeException {

  @Serial private static final long serialVersionUID = 3257848770627713076L;

  public SchemaCrawlerException(final String message) {
    super(message);
  }

  public SchemaCrawlerException(final String message, final Throwable cause) {
    super(makeExceptionMessage(message, cause), cause);
  }

  public SchemaCrawlerException(final Throwable cause) {
    super(makeExceptionMessage(null, cause), cause);
  }
}
