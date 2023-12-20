/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.command.serialize.model.CatalogDescription;
import schemacrawler.tools.command.serialize.model.CompactCatalogUtility;

/** Decorates a database to allow for serialization to a compact JSON format. */
public final class CompactSerializedCatalog implements CatalogSerializer {

  private static final Logger LOGGER = Logger.getLogger(CompactSerializedCatalog.class.getName());

  private final Catalog catalog;
  private final CatalogDescription catalogDescription;

  public CompactSerializedCatalog(final Catalog catalog) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
    this.catalogDescription = CompactCatalogUtility.createCatalogDescription(catalog);
  }

  @Override
  public Catalog getCatalog() {
    return catalog;
  }

  /** {@inheritDoc} */
  @Override
  public void save(final OutputStream out) {
    requireNonNull(out, "No output stream provided");
    save(new OutputStreamWriter(out, UTF_8));
  }

  /** {@inheritDoc} */
  @Override
  public void save(final Writer out) {
    requireNonNull(out, "No writer provided");
    try {
      final ObjectMapper mapper = new ObjectMapper();
      mapper.writeValue(out, catalogDescription);
    } catch (final IOException e) {
      throw new IORuntimeException("Could not serialize catalog", e);
    }
  }
}
