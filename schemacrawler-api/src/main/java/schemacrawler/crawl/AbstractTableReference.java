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

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableReference;
import us.fatehi.utility.CompareUtility;

/** Represents a foreign-key mapping to a primary key in another table. */
abstract class AbstractTableReference extends AbstractNamedObjectWithAttributes
    implements TableReference {

  private final class MemoState implements Serializable {

    private static final long serialVersionUID = -8137191924777748304L;

    private final Table pkTable;
    private final Table fkTable;
    private final List<TableConstraintColumn> tableConstraintColumns;

    MemoState() {
      Table pkTable = null;
      Table fkTable = null;

      final List<ColumnReference> columnReferences =
          new ArrayList<>(AbstractTableReference.this.columnReferences);
      tableConstraintColumns = new ArrayList<>();
      for (int i = 0; i < columnReferences.size(); i++) {

        final ColumnReference columnReference = columnReferences.get(i);
        if (i == 0) {
          pkTable = columnReference.getPrimaryKeyColumn().getParent();
          fkTable = columnReference.getForeignKeyColumn().getParent();
        }
        final Column fkColumn = columnReference.getForeignKeyColumn();
        final MutableTableConstraintColumn tableConstraintColumn =
            new MutableTableConstraintColumn(AbstractTableReference.this, fkColumn);
        tableConstraintColumn.setKeyOrdinalPosition(i + 1);
        tableConstraintColumns.add(tableConstraintColumn);
      }
      this.pkTable = requireNonNull(pkTable, "No primary table found");
      this.fkTable = requireNonNull(fkTable, "No foreign table found");
    }

    Table getForeignKeyTable() {
      return fkTable;
    }

    Table getPrimaryKeyTable() {
      return pkTable;
    }

    List<TableConstraintColumn> getTableConstraintColumns() {
      return new ArrayList<>(tableConstraintColumns);
    }
  }

  private static final long serialVersionUID = -5164664131926303038L;

  private final SortedSet<ColumnReference> columnReferences;
  private transient MemoState state;

  public AbstractTableReference(final String name) {
    super(name);
    columnReferences = new TreeSet<>();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Note: Since foreign keys are not always explicitly named in databases, the sorting routine
   * orders the foreign keys by the names of the columns in the foreign keys.
   */
  @Override
  public int compareTo(final NamedObject obj) {
    if (obj == null) {
      return -1;
    }

    if (obj instanceof TableReference) {
      final TableReference other = (TableReference) obj;
      final List<ColumnReference> thisColumnReferences = getColumnReferences();
      final List<ColumnReference> otherColumnReferences = other.getColumnReferences();

      return CompareUtility.compareLists(thisColumnReferences, otherColumnReferences);
    }

    if (obj instanceof NamedObject) {
      final NamedObject other = obj;
      return super.compareTo(other);
    }

    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof TableReference)) {
      return false;
    }
    final TableReference other = (TableReference) obj;
    return Objects.equals(getColumnReferences(), other.getColumnReferences());
  }

  /** {@inheritDoc} */
  @Override
  public List<ColumnReference> getColumnReferences() {
    return new ArrayList<>(columnReferences);
  }

  @Override
  public List<TableConstraintColumn> getConstrainedColumns() {
    buildState();
    return state.getTableConstraintColumns();
  }

  @Override
  public Table getForeignKeyTable() {
    buildState();
    return state.getForeignKeyTable();
  }

  @Override
  public Table getParent() {
    return getForeignKeyTable();
  }

  @Override
  public Table getPrimaryKeyTable() {
    buildState();
    return state.getPrimaryKeyTable();
  }

  /** Gets the schema of the constrained table - that is the referencing table. */
  @Override
  public Schema getSchema() {
    buildState();
    return state.getForeignKeyTable().getSchema();
  }

  @Override
  public String getShortName() {
    return getName();
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return hash(columnReferences);
  }

  @Override
  public final boolean isParentPartial() {
    return getParent() instanceof PartialDatabaseObject;
  }

  /** {@inheritDoc} */
  @Override
  public Iterator<ColumnReference> iterator() {
    return columnReferences.iterator();
  }

  void addColumnReference(final ColumnReference columnReference) {
    if (columnReference != null) {
      columnReferences.add(columnReference);
    }
  }

  private void buildState() {
    if (state == null) {
      state = new MemoState();
    }
  }
}
