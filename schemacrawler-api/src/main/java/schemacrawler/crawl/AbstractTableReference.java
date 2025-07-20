/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableReference;
import us.fatehi.utility.CompareUtility;
import us.fatehi.utility.string.StringFormat;

/** Represents a foreign-key mapping to a primary key in another table. */
abstract class AbstractTableReference extends MutableTableConstraint implements TableReference {

  private static final long serialVersionUID = -5164664131926303038L;

  private static final Logger LOGGER = Logger.getLogger(AbstractTableReference.class.getName());

  private final Table pkTable;
  private final SortedSet<ColumnReference> columnReferences;

  public AbstractTableReference(final String name, final ColumnReference columnReference) {
    super(
        requireNonNull(columnReference, "No column reference provided")
            .getForeignKeyColumn()
            .getParent(),
        name);

    pkTable = columnReference.getPrimaryKeyColumn().getParent();
    columnReferences = new TreeSet<>();
    addColumnReference(columnReference);
  }

  /**
   * {@inheritDoc}
   *
   * <p>NOTE: compareTo is not compatible with equals. equals compares the full name of a database
   * object, but compareTo uses more fields to define a "natural" sorting order. compareTo may
   * return incorrect results until the object is fully built by SchemaCrawler.
   *
   * <p>Since foreign keys are not always explicitly named in databases, the sorting routine orders
   * the foreign keys by the names of the columns in the foreign keys.
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
  public List<ColumnReference> getColumnReferences() {
    return new ArrayList<>(columnReferences);
  }

  @Override
  public Table getForeignKeyTable() {
    return getParent();
  }

  @Override
  public Table getPrimaryKeyTable() {
    return pkTable;
  }

  /** {@inheritDoc} */
  @Override
  public Iterator<ColumnReference> iterator() {
    return columnReferences.iterator();
  }

  boolean addColumnReference(final ColumnReference columnReference) {
    if (columnReference == null) {
      return false;
    }
    // Add a column reference only if they reference the same two tables
    final Table fkTable = getParent();
    if (pkTable.equals(columnReference.getPrimaryKeyColumn().getParent())
        && fkTable.equals(columnReference.getForeignKeyColumn().getParent())) {
      columnReferences.add(columnReference);
      addTableConstraintColumn(columnReference);
      return true;
    }
    LOGGER.log(
        Level.CONFIG,
        new StringFormat(
            "Column reference <%s> not added, since it is not consistent with <%s --> %s>",
            columnReference, fkTable, pkTable));
    return false;
  }

  private void addTableConstraintColumn(final ColumnReference columnReference) {
    final Column fkColumn = columnReference.getForeignKeyColumn();
    final MutableTableConstraintColumn tableConstraintColumn =
        new MutableTableConstraintColumn(AbstractTableReference.this, fkColumn);
    tableConstraintColumn.setKeyOrdinalPosition(columnReference.getKeySequence());
    addColumn(tableConstraintColumn);
  }
}
