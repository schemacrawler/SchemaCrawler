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
import java.util.Optional;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;
import us.fatehi.utility.CompareUtility;

/** Represents an index on a database table. */
class MutableIndex extends AbstractDependantObject<Table> implements Index {

  private static final long serialVersionUID = 4051326747138079028L;

  private final NamedObjectList<MutableIndexColumn> columns = new NamedObjectList<>();
  private final StringBuilder definition;
  private long cardinality;
  private IndexType indexType;
  private boolean isUnique;
  private long pages;

  MutableIndex(final Table parent, final String name) {
    super(new TablePointer(parent), name);
    // Default values
    indexType = IndexType.unknown;
    definition = new StringBuilder();
  }

  /**
   * {@inheritDoc}
   *
   * <p>NOTE: compareTo is not compatible with equals. equals compares the full name of a database
   * object, but compareTo uses more fields to define a "natural" sorting order. compareTo may
   * return incorrect results until the object is fully built by SchemaCrawler.
   *
   * <p>Note: Since indexes are not always explicitly named in databases, the sorting routine orders
   * the indexes by the names of the columns in the index.
   */
  @Override
  public final int compareTo(final NamedObject obj) {
    if (obj == null || !(obj instanceof Index)) {
      return -1;
    }

    final Index that = (Index) obj;

    int compareTo = 0;

    if (compareTo == 0) {
      final List<IndexColumn> thisColumns = getColumns();
      final List<IndexColumn> thatColumns = that.getColumns();

      compareTo = CompareUtility.compareLists(thisColumns, thatColumns);
    }

    if (compareTo == 0 && isUnique != that.isUnique()) {
      compareTo = isUnique ? -1 : 1;
    }

    if (compareTo == 0) {
      compareTo = indexType.ordinal() - that.getIndexType().ordinal();
    }

    if (compareTo == 0) {
      compareTo = super.compareTo(obj);
    }

    return compareTo;
  }

  /** {@inheritDoc} */
  @Override
  public final long getCardinality() {
    return cardinality;
  }

  /** {@inheritDoc} */
  @Override
  public final List<IndexColumn> getColumns() {
    return new ArrayList<>(columns.values());
  }

  /** {@inheritDoc} */
  @Override
  public final String getDefinition() {
    return definition.toString();
  }

  /** {@inheritDoc} */
  @Override
  public final IndexType getIndexType() {
    return indexType;
  }

  /** {@inheritDoc} */
  @Override
  public final long getPages() {
    return pages;
  }

  /** {@inheritDoc} */
  @Override
  public final IndexType getType() {
    return getIndexType();
  }

  @Override
  public final boolean hasDefinition() {
    return definition.length() > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isUnique() {
    return isUnique;
  }

  @Override
  public final Iterator<IndexColumn> iterator() {
    return getColumns().iterator();
  }

  /** {@inheritDoc} */
  @Override
  public final Optional<MutableIndexColumn> lookupColumn(final String name) {
    // NOTE: Index columns are still table columns, so they need to be
    // looked up with a table lookup key
    return columns.lookup(getParent(), name);
  }

  final void addColumn(final MutableIndexColumn column) {
    columns.add(column);
  }

  final void appendDefinition(final String definition) {
    if (definition != null) {
      this.definition.append(definition);
    }
  }

  final void setCardinality(final long cardinality) {
    this.cardinality = cardinality;
  }

  final void setIndexType(final IndexType indexType) {
    this.indexType = requireNonNull(indexType, "Null index type");
  }

  final void setPages(final long pages) {
    this.pages = pages;
  }

  final void setUnique(final boolean unique) {
    isUnique = unique;
  }
}
