/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.ioresource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConsoleOutputResource implements OutputResource {

  private static final Logger LOGGER = Logger.getLogger(ConsoleOutputResource.class.getName());

  @Override
  public Writer openNewOutputWriter(final Charset charset, final boolean appendOutput)
      throws IOException {
    final Writer writer = new BufferedWriter(new OutputStreamWriter(System.out, charset));
    LOGGER.log(Level.FINE, "Opened output writer to console");
    // Console should not be closed
    return new NonCloseableWriter(writer);
  }

  @Override
  public String toString() {
    return "<console>";
  }
}
