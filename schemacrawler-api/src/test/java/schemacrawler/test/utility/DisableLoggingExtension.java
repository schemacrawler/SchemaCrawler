/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import us.fatehi.utility.LoggingConfig;

public class DisableLoggingExtension implements BeforeAllCallback {

  @Override
  public void beforeAll(final ExtensionContext context) throws Exception {
    // Turn off logging
    new LoggingConfig();
  }
}
