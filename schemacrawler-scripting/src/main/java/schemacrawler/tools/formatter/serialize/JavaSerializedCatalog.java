/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.formatter.serialize;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;

/** Decorates a database to allow for serialization to and from plain Java serialization. */
public final class JavaSerializedCatalog implements CatalogSerializer {

  private static Catalog readCatalog(final InputStream in) {
    requireNonNull(in, "No input stream provided");
    try (final CatalogModelInputStream objIn = new CatalogModelInputStream(in)) {
      return (Catalog) objIn.readObject();
    } catch (ClassNotFoundException | IOException e) {
      throw new ExecutionRuntimeException("Cannot deserialize catalog", e);
    }
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
    try (final ObjectOutputStream objOut = new ObjectOutputStream(out)) {
      objOut.writeObject(catalog);
    } catch (final IOException e) {
      throw new IORuntimeException("Could not serialize catalog", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void save(final Writer out) {
    throw new UnsupportedOperationException("Cannot serialize binary format using character data");
  }
}
