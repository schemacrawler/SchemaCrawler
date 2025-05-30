/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.formatter.serialize;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;

/** Decorates a database to allow for serialization to and from Kryo serialization. */
public final class KryoSerializedCatalog implements CatalogSerializer {

  private static Kryo initializeKryo() {
    final Kryo kryo = new Kryo();

    kryo.setAutoReset(true);
    kryo.setCopyReferences(true);
    kryo.setReferences(true);

    kryo.register(Catalog.class);
    kryo.register(Table.class);
    kryo.register(Routine.class);

    return kryo;
  }

  private static Catalog readCatalog(final InputStream in) {
    requireNonNull(in, "No input stream provided");
    final Kryo kryo = initializeKryo();
    try (final Input input = new Input(in)) {
      final Catalog catalog = kryo.readObject(input, Catalog.class);
      return catalog;
    }
  }

  private final Catalog catalog;

  public KryoSerializedCatalog(final Catalog catalog) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public KryoSerializedCatalog(final InputStream in) {
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

    final Kryo kryo = initializeKryo();
    try (final Output output = new Output(out)) {
      kryo.writeObject(output, catalog);
      output.flush();
    }
  }

  /** {@inheritDoc} */
  @Override
  public void save(final Writer out) {
    throw new UnsupportedOperationException("Cannot serialize binary format using character data");
  }
}
