/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler.exceptions;

import java.io.IOException;

public class IORuntimeException extends SchemaCrawlerException {

  private static final long serialVersionUID = 8143604098031489051L;

  public IORuntimeException(final String message) {
    super(message);
  }

  public IORuntimeException(final String message, final IOException cause) {
    super(message, cause);
  }
}
