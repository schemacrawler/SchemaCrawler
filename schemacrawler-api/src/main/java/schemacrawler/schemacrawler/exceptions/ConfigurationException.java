/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler.exceptions;

import java.io.Serial;

public class ConfigurationException extends SchemaCrawlerException {

  @Serial private static final long serialVersionUID = 8143604098031489051L;

  public ConfigurationException(final String message) {
    super(message);
  }

  public ConfigurationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
