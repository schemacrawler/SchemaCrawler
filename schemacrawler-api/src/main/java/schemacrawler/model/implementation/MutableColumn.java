/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import schemacrawler.schema.Column;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Table;

/**
 * Represents a column in a database table or routine. Created from metadata returned by a JDBC
 * call.
 */
public final class MutableColumn extends AbstractColumn<Table> implements Column {

  @Serial private static final long serialVersionUID = 3834591019449528633L;
  private final NamedObjectList<MutablePrivilege<Column>> privileges = new NamedObjectList<>();
  private String defaultValue;
  private boolean isAutoIncremented;
  private boolean isGenerated;
  private boolean isHidden;
  private boolean isPartOfIndex;
  private boolean isPartOfPrimaryKey;
  private boolean isPartOfUniqueIndex;
  private Column referencedColumn;

  public MutableColumn(final Table parent, final String name) {
    super(new TablePointer(parent), name);
  }

  /** {@inheritDoc} */
  @Override
  public String getDefaultValue() {
    return defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Privilege<Column>> getPrivileges() {
    return new ArrayList<>(privileges.values());
  }

  /** {@inheritDoc} */
  @Override
  public Column getReferencedColumn() {
    return referencedColumn;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAutoIncremented() {
    return isAutoIncremented;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isGenerated() {
    return isGenerated;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isHidden() {
    return isHidden;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPartOfForeignKey() {
    return referencedColumn != null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPartOfIndex() {
    return isPartOfIndex;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPartOfPrimaryKey() {
    return isPartOfPrimaryKey;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPartOfUniqueIndex() {
    return isPartOfUniqueIndex;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutablePrivilege<Column>> lookupPrivilege(final String name) {
    return privileges.lookup(this, name);
  }

  public void addPrivilege(final MutablePrivilege<Column> privilege) {
    privileges.add(privilege);
  }

  public void markAsPartOfIndex() {
    isPartOfIndex = true;
  }

  public void markAsPartOfPrimaryKey() {
    isPartOfPrimaryKey = true;
  }

  public void markAsPartOfUniqueIndex() {
    isPartOfUniqueIndex = true;
  }

  public void setAutoIncremented(final boolean isAutoIncremented) {
    this.isAutoIncremented = isAutoIncremented;
  }

  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void setGenerated(final boolean isGenerated) {
    this.isGenerated = isGenerated;
  }

  public void setHidden(final boolean isHidden) {
    this.isHidden = isHidden;
  }

  public void setReferencedColumn(final Column referencedColumn) {
    this.referencedColumn = referencedColumn;
  }
}
