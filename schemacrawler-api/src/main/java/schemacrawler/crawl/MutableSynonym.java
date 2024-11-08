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

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Synonym;

/**
 * Represents a database synonym. Created from metadata returned by a JDBC call.
 *
 * <p>(Based on an idea from Matt Albrecht)
 */
final class MutableSynonym extends AbstractDatabaseObject implements Synonym {

  private static final long serialVersionUID = -5980593047288755771L;

  private DatabaseObject referencedObject;

  MutableSynonym(final Schema schema, final String name) {
    super(schema, name);
  }

  @Override
  public DatabaseObject getReferencedObject() {
    return referencedObject;
  }

  void setReferencedObject(final DatabaseObject referencedObject) {
    this.referencedObject = requireNonNull(referencedObject, "Referenced object not provided");
  }
}
