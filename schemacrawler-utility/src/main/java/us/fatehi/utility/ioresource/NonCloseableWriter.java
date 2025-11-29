/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.ioresource;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

final class NonCloseableWriter extends FilterWriter {

  NonCloseableWriter(final Writer out) {
    super(out);
  }

  /** Flush but do not close. */
  @Override
  public void close() throws IOException {
    super.flush();
  }

  @Override
  public String toString() {
    return out.toString();
  }
}
