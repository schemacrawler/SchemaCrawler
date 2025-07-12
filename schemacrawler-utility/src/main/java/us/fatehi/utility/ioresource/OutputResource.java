/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.ioresource;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

@FunctionalInterface
public interface OutputResource {

  default String getDescription() {
    return toString();
  }

  Writer openNewOutputWriter(Charset charset, boolean appendOutput) throws IOException;
}
