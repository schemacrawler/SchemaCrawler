/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;

import static us.fatehi.utility.Utility.isBlank;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableConstraintType;

/** Represents a table constraint. */
public class MutableTableConstraint extends AbstractDependantObject<Table> implements TableConstraint {

  @Serial private static final long serialVersionUID = 1155277343302693656L;

  private final NamedObjectList<MutableTableConstraintColumn> columns = new NamedObjectList<>();
  private String definition;
  private boolean deferrable;
  private boolean initiallyDeferred;

  private TableConstraintType tableConstraintType;

  public MutableTableConstraint(final Table parent, final String name) {
    super(new TablePointer(parent), name);
    definition = "";
  }

  /** {@inheritDoc} */
  @Override
  public List<TableConstraintColumn> getConstrainedColumns() {
    return new ArrayList<>(columns.values());
  }

  /** {@inheritDoc} */
  @Override
  public String getDefinition() {
    return definition;
  }

  @Override
  public TableConstraintType getType() {
    if (tableConstraintType != null) {
      return tableConstraintType;
    }
    return TableConstraintType.unknown;
  }

  @Override
  public final boolean hasDefinition() {
    return !isBlank(definition);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isDeferrable() {
    return deferrable;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isInitiallyDeferred() {
    return initiallyDeferred;
  }

  public final void addColumn(final MutableTableConstraintColumn column) {
    columns.add(column);
  }

  public final void setDeferrable(final boolean deferrable) {
    this.deferrable = deferrable;
  }

  public final void setDefinition(final String definition) {
    if (!hasDefinition() && !isBlank(definition)) {
      this.definition = definition;
    }
  }

  public final void setInitiallyDeferred(final boolean initiallyDeferred) {
    this.initiallyDeferred = initiallyDeferred;
  }

  public final void setTableConstraintType(final TableConstraintType tableConstraintType) {
    this.tableConstraintType = tableConstraintType;
  }
}
