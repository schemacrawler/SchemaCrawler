/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.utility;

import static us.fatehi.utility.Utility.isBlank;

import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.schemacrawler.exceptions.WrappedSQLException;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class ExceptionUtility {

  public static String makeExceptionMessage(final String message, final Throwable cause) {
    final String causeMessage;
    if (cause == null) {
      causeMessage = "";
    } else {
      causeMessage = cause.getMessage();
    }

    // If the override message is blank, use the cause message, even if the cause message is null
    if (isBlank(message)) {
      return causeMessage;
    }
    // If the cause message is blank, use the override message, even if the it is null
    if (isBlank(causeMessage)) {
      return message;
    }
    // Do not use a combined message for known exceptions, since they may already be mapped
    if (cause instanceof SchemaCrawlerException || cause instanceof WrappedSQLException) {
      return message;
    }
    // Use a combined message
    return message + ": " + causeMessage;
  }

  private ExceptionUtility() {
    // Prevent instantiation
  }
}
