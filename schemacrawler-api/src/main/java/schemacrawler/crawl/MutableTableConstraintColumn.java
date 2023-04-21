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

package schemacrawler.crawl;

import schemacrawler.schema.Column;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;

final class MutableTableConstraintColumn extends MutableKeyColumn implements TableConstraintColumn {

  private static final long serialVersionUID = -6923211341742623556L;

  private final TableConstraint tableConstraint;

  MutableTableConstraintColumn(final TableConstraint tableConstraint, final Column column) {
    super(column);
    this.tableConstraint = tableConstraint;
  }

  /** {@inheritDoc} */
  @Override
  public TableConstraint getTableConstraint() {
    return tableConstraint;
  }

  /** {@inheritDoc} */
  @Override
  public int getTableConstraintOrdinalPosition() {
    return getKeyOrdinalPosition();
  }
}
