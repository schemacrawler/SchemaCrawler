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

package schemacrawler.loader.weakassociations;

import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.PartialDatabaseObject;

public final class ProposedWeakAssociation implements ColumnReference {

  private static final long serialVersionUID = 2986663326992262188L;

  private final Column primaryKeyColumn;
  private final Column foreignKeyColumn;

  ProposedWeakAssociation(final Column foreignKeyColumn, final Column primaryKeyColumn) {
    this.primaryKeyColumn = requireNonNull(primaryKeyColumn, "No primary key column provided");
    this.foreignKeyColumn = requireNonNull(foreignKeyColumn, "No foreign key column provided");
  }

  @Override
  public int compareTo(final ColumnReference o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Column getForeignKeyColumn() {
    return foreignKeyColumn;
  }

  @Override
  public int getKeySequence() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Column getPrimaryKeyColumn() {
    return primaryKeyColumn;
  }

  public boolean isValid() {

    if (primaryKeyColumn.equals(foreignKeyColumn)) {
      return false;
    }

    final boolean isPkColumnPartial = primaryKeyColumn instanceof PartialDatabaseObject;
    final boolean isFkColumnPartial = foreignKeyColumn instanceof PartialDatabaseObject;
    if ((isFkColumnPartial && isPkColumnPartial)
        || !primaryKeyColumn.isColumnDataTypeKnown()
        || !foreignKeyColumn.isColumnDataTypeKnown()) {
      return false;
    }

    final ColumnDataType fkColumnType = foreignKeyColumn.getColumnDataType();
    final ColumnDataType pkColumnType = primaryKeyColumn.getColumnDataType();
    final boolean isValid =
        fkColumnType.getStandardTypeName().equals(pkColumnType.getStandardTypeName());
    return isValid;
  }

  @Override
  public String toString() {
    return foreignKeyColumn + " ~~> " + primaryKeyColumn;
  }
}
