/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.ioresource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public interface InputResource {

  default String getDescription() {
    return toString();
  }

  InputStream openNewInputStream() throws IOException;

  BufferedReader openNewInputReader(Charset charset) throws IOException;
}
