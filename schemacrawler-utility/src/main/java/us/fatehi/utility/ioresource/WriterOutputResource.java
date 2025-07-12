/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.ioresource;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;

public final class WriterOutputResource implements OutputResource {

  private static final Logger LOGGER = Logger.getLogger(WriterOutputResource.class.getName());

  private final Writer writer;

  public WriterOutputResource(final Writer writer) {
    this.writer = requireNonNull(writer, "No writer provided");
  }

  @Override
  public Writer openNewOutputWriter(final Charset charset, final boolean appendOutput) {
    LOGGER.log(Level.FINE, "Output to provided writer");
    // Since the original write was provided to us,
    // we should not allow it to be closed
    return new NonCloseableWriter(writer);
  }

  @Override
  public String toString() {
    return "<writer>";
  }
}
