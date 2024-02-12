/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Objects.requireNonNull;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.tools.command.serialize.model.CompactCatalogUtility;
import schemacrawler.tools.command.serialize.model.TableDocument;

public final class EmbeddedTable implements NamedObject {

  private static final long serialVersionUID = 5216101777323983303L;

  private final Table table;
  private final TableDocument tableDocument;
  private TextEmbedding embedding;

  EmbeddedTable(final Table table) {
    this.table = requireNonNull(table, "No table provided");
    tableDocument = CompactCatalogUtility.getTableDocument(table, false);
  }

  @Override
  public int compareTo(final NamedObject object) {
    return table.compareTo(object);
  }

  public TextEmbedding getEmbedding() {
    return embedding;
  }

  @Override
  public String getFullName() {
    return table.getFullName();
  }

  @Override
  public String getName() {
    return table.getName();
  }

  public Schema getSchema() {
    return table.getSchema();
  }

  public boolean hasEmbedding() {
    return embedding != null;
  }

  @Override
  public NamedObjectKey key() {
    return table.key();
  }

  public String toJson() {
    return tableDocument.toJson();
  }

  @Override
  public String toString() {
    return getFullName();
  }

  void setEmbedding(final TextEmbedding providedEmmedding) {
    embedding = providedEmmedding;
  }
}
