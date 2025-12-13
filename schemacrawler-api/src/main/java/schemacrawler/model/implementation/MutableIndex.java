/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;
import us.fatehi.utility.CollectionsUtility;

/** Represents an index on a database table. */
public class MutableIndex extends AbstractDependantObject<Table> implements Index {

  @Serial private static final long serialVersionUID = 4051326747138079028L;

  private final NamedObjectList<MutableIndexColumn> columns = new NamedObjectList<>();
  private String definition;
  private long cardinality;
  private IndexType indexType;
  private boolean isUnique;
  private long pages;
  private String filterCondition;

  public MutableIndex(final Table parent, final String name) {
    super(new TablePointer(parent), name);
    // Default values
    indexType = IndexType.unknown;
    definition = "";
    filterCondition = "";
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

      compareTo = CollectionsUtility.compareLists(thisColumns, thatColumns);
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

  @Override
  public final String getFilterCondition() {
    return filterCondition;
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
    return !isBlank(definition);
  }

  @Override
  public final boolean hasFilterCondition() {
    return !isBlank(filterCondition);
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

  public final void addColumn(final MutableIndexColumn column) {
    columns.add(column);
  }

  public final void setCardinality(final long cardinality) {
    this.cardinality = cardinality;
  }

  public final void setDefinition(final String definition) {
    if (!hasDefinition() && !isBlank(definition)) {
      this.definition = definition;
    }
  }

  public final void setFilterCondition(final String filterCondition) {
    if (!hasFilterCondition() && !isBlank(filterCondition)) {
      this.filterCondition = filterCondition;
    }
  }

  public final void setIndexType(final IndexType indexType) {
    this.indexType = requireNonNull(indexType, "Null index type");
  }

  public final void setPages(final long pages) {
    this.pages = pages;
  }

  public final void setUnique(final boolean unique) {
    isUnique = unique;
  }
}
