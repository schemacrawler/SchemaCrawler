/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.formatter.serialize;

import java.io.OutputStream;
import java.io.Writer;
import schemacrawler.schema.Catalog;

public interface CatalogSerializer {

  /**
   * Gets the catalog wrapped by ths savable.
   *
   * @return Catalog
   */
  Catalog getCatalog();

  /**
   * Serialize catalog to a binary stream. If the serialization format is text-based, specified
   * character encoding will not be honored.
   *
   * @param out Output stream
   */
  void save(final OutputStream out);

  /**
   * Serialize catalog to a binary stream. If the serialization format is text-based, specified
   * character encoding will be honored. If the serialization format is binary, and exception will
   * be thrown.
   *
   * @param out Output stream
   */
  void save(final Writer out);
}
