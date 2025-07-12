/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

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
final class MutableColumn extends AbstractColumn<Table> implements Column {

  private static final long serialVersionUID = 3834591019449528633L;
  private final NamedObjectList<MutablePrivilege<Column>> privileges = new NamedObjectList<>();
  private String defaultValue;
  private boolean isAutoIncremented;
  private boolean isGenerated;
  private boolean isHidden;
  private boolean isPartOfIndex;
  private boolean isPartOfPrimaryKey;
  private boolean isPartOfUniqueIndex;
  private Column referencedColumn;

  MutableColumn(final Table parent, final String name) {
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

  void addPrivilege(final MutablePrivilege<Column> privilege) {
    privileges.add(privilege);
  }

  void markAsPartOfIndex() {
    isPartOfIndex = true;
  }

  void markAsPartOfPrimaryKey() {
    isPartOfPrimaryKey = true;
  }

  void markAsPartOfUniqueIndex() {
    isPartOfUniqueIndex = true;
  }

  void setAutoIncremented(final boolean isAutoIncremented) {
    this.isAutoIncremented = isAutoIncremented;
  }

  void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

  void setGenerated(final boolean isGenerated) {
    this.isGenerated = isGenerated;
  }

  void setHidden(final boolean isHidden) {
    this.isHidden = isHidden;
  }

  void setReferencedColumn(final Column referencedColumn) {
    this.referencedColumn = referencedColumn;
  }
}
