/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.Objects;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;

/** Represents a single column mapping from a primary key column to a foreign key column. */
final class ImmutableColumnReference implements ColumnReference {

  private static final long serialVersionUID = -4411771492159843382L;

  private final Column foreignKeyColumn;
  private final Column primaryKeyColumn;
  private final int keySequence;

  protected ImmutableColumnReference(
      final int keySequence, final Column foreignKeyColumn, final Column primaryKeyColumn) {
    this.keySequence = keySequence;
    this.foreignKeyColumn = requireNonNull(foreignKeyColumn, "No foreign key column provided");
    this.primaryKeyColumn = requireNonNull(primaryKeyColumn, "No primary key column provided");
  }

  /**
   * {@inheritDoc}
   *
   * <p>NOTE: compareTo is not compatible with equals. equals compares the full name of a database
   * object, but compareTo uses more fields to define a "natural" sorting order. compareTo may
   * return incorrect results until the object is fully built by SchemaCrawler.
   */
  @Override
  public int compareTo(final ColumnReference columnRef) {

    if (columnRef == null) {
      return -1;
    }

    int compare = 0;

    final ColumnReference other = columnRef;
    if (compare == 0) {
      compare = getKeySequence() - other.getKeySequence();
    }
    if (compare == 0) {
      compare =
          foreignKeyColumn.getFullName().compareTo(columnRef.getForeignKeyColumn().getFullName());
    }
    if (compare == 0) {
      compare =
          primaryKeyColumn.getFullName().compareTo(columnRef.getPrimaryKeyColumn().getFullName());
    }
    return compare;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ImmutableColumnReference)) {
      return false;
    }
    final ColumnReference other = (ColumnReference) obj;
    return Objects.equals(primaryKeyColumn, other.getPrimaryKeyColumn())
        && Objects.equals(foreignKeyColumn, other.getForeignKeyColumn());
  }

  /** {@inheritDoc} */
  @Override
  public Column getForeignKeyColumn() {
    return foreignKeyColumn;
  }

  /** {@inheritDoc} */
  @Override
  public int getKeySequence() {
    return keySequence;
  }

  /** {@inheritDoc} */
  @Override
  public Column getPrimaryKeyColumn() {
    return primaryKeyColumn;
  }

  @Override
  public int hashCode() {
    return Objects.hash(foreignKeyColumn, primaryKeyColumn);
  }

  @Override
  public String toString() {
    return foreignKeyColumn + " --> " + primaryKeyColumn;
  }
}
