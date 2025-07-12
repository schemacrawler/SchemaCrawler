/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.formatter.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import static java.util.Objects.requireNonNull;
import us.fatehi.utility.string.StringFormat;

final class CatalogModelInputStream extends ObjectInputStream {

  private static final Logger LOGGER = Logger.getLogger(CatalogModelInputStream.class.getName());

  private final List<Pattern> acceptPatterns =
      Arrays.asList(
          Pattern.compile("us\\.fatehi\\.utility\\.property\\.[A-Z].*"),
          Pattern.compile("schemacrawler\\.(schema(crawler)?|crawl)\\.[A-Z].*"),
          Pattern.compile("schemacrawler\\.[A-Z].*"),
          Pattern.compile("(\\[L)?java\\.(lang|util)\\..*"),
          Pattern.compile("java\\.(sql|math|time|net)\\..*"),
          Pattern.compile("\\[[BC]"));

  CatalogModelInputStream(final InputStream input) throws IOException {
    super(requireNonNull(input, "No input stream provided"));
  }

  @Override
  protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass)
      throws IOException, ClassNotFoundException {
    validateClassName(objectStreamClass.getName());
    return super.resolveClass(objectStreamClass);
  }

  private void validateClassName(final String className) throws InvalidClassException {
    for (final Pattern pattern : acceptPatterns) {
      if (pattern.matcher(className).matches()) {
        LOGGER.log(Level.FINER, new StringFormat("Deserializing class <%s>", className));
        return;
      }
    }
    throw new InvalidClassException(String.format("Not deserializing class <%s>", className));
  }
}
