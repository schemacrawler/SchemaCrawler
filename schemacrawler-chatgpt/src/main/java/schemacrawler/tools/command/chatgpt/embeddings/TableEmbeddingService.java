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

package schemacrawler.tools.command.chatgpt.embeddings;

import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Table;
import us.fatehi.utility.string.StringFormat;

public final class TableEmbeddingService {

  private static final Logger LOGGER =
      Logger.getLogger(TableEmbeddingService.class.getCanonicalName());

  private final EmbeddingService service;

  public TableEmbeddingService(final EmbeddingService service) {
    this.service = requireNonNull(service, "No embedding service provided");
  }

  public EmbeddedTable embedTable(final Table table) {
    requireNonNull(table, "No table provided");

    LOGGER.log(Level.FINE, new StringFormat("Emebedding table <%s>", table));

    final EmbeddedTable embeddedTable = new EmbeddedTable(table);
    embeddedTable.setEmbedding(service.embed(embeddedTable.toJson()));
    return embeddedTable;
  }
}
