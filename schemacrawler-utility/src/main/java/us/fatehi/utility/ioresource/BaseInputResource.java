/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.ioresource;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class BaseInputResource implements InputResource {

  private static final Logger LOGGER = Logger.getLogger(BaseInputResource.class.getName());

  @Override
  public final BufferedReader openNewInputReader(final Charset charset) throws IOException {
    requireNonNull(charset, "No input charset provided");

    final InputStream inputStream = openNewInputStream();

    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
    LOGGER.log(Level.FINE, "Opened resource <%s> for reading".formatted(getDescription()));

    return reader;
  }
}
