/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.formatter.serialize;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import schemacrawler.schema.Catalog;
import schemacrawler.utility.SerializedCatalogUtility;

/** Decorates a database to allow for serialization to and from plain Java serialization. */
public final class JavaSerializedCatalog implements CatalogSerializer {

  private static Catalog readCatalog(final InputStream in) {
    requireNonNull(in, "No input stream provided");
    return SerializedCatalogUtility.readCatalog(in);
  }

  private final Catalog catalog;

  public JavaSerializedCatalog(final Catalog catalog) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public JavaSerializedCatalog(final InputStream in) {
    this(readCatalog(in));
  }

  @Override
  public Catalog getCatalog() {
    return catalog;
  }

  /** {@inheritDoc} */
  @Override
  public void save(final OutputStream out) {
    requireNonNull(out, "No output stream provided");
    SerializedCatalogUtility.saveCatalog(catalog, out);
  }

  /** {@inheritDoc} */
  @Override
  public void save(final Writer out) {
    throw new UnsupportedOperationException("Cannot serialize binary format using character data");
  }
}
