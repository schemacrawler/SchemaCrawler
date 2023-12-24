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

package schemacrawler.crawl;

import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.TableConstraintType;
import schemacrawler.schema.WeakAssociation;

/** Represents a foreign-key mapping to a primary key in another table. */
final class MutableWeakAssociation extends AbstractTableReference implements WeakAssociation {

  private static final long serialVersionUID = -5164664131926303038L;

  public MutableWeakAssociation(final String name, final ColumnReference columnReference) {
    super(name, columnReference);
  }

  @Override
  public String getDefinition() {
    return "";
  }

  @Override
  public TableConstraintType getType() {
    return TableConstraintType.weak_association;
  }

  @Override
  public boolean hasDefinition() {
    return false;
  }

  @Override
  public boolean isDeferrable() {
    return false;
  }

  @Override
  public boolean isInitiallyDeferred() {
    return false;
  }
}
