/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.TableType;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.WeakAssociation;

final class TablePartial extends AbstractDatabaseObject implements Table, PartialDatabaseObject {

  private static final long serialVersionUID = -5968964551235088703L;

  private Column column;
  private ForeignKey foreignKey;

  TablePartial(final Schema schema, final String tableName) {
    super(schema, tableName);
  }

  TablePartial(final Table table) {
    this(requireNonNull(table, "No table provided").getSchema(), table.getName());
    addAttributes(table.getAttributes());
  }

  @Override
  public Collection<PrimaryKey> getAlternateKeys() {
    throw new NotLoadedException(this);
  }

  @Override
  public List<Column> getColumns() {
    throw new NotLoadedException(this);
  }

  @Override
  public String getDefinition() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<ForeignKey> getExportedForeignKeys() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<ForeignKey> getForeignKeys() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<Column> getHiddenColumns() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<ForeignKey> getImportedForeignKeys() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<Index> getIndexes() {
    throw new NotLoadedException(this);
  }

  @Override
  public PrimaryKey getPrimaryKey() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<Privilege<Table>> getPrivileges() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<DatabaseObject> getUsingObjects() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<Table> getRelatedTables(final TableRelationshipType tableRelationshipType) {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<TableConstraint> getTableConstraints() {
    throw new NotLoadedException(this);
  }

  @Override
  public TableType getTableType() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<Trigger> getTriggers() {
    throw new NotLoadedException(this);
  }

  @Override
  public TableType getType() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<WeakAssociation> getWeakAssociations() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean hasDefinition() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean hasForeignKeys() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean hasIndexes() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasPrimaryKey() {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean hasTriggers() {
    return false;
  }

  @Override
  public Optional<PrimaryKey> lookupAlternateKey(final String name) {
    throw new NotLoadedException(this);
  }

  @Override
  public Optional<Column> lookupColumn(final String name) {
    if (column.getName().equals(name)) {
      return Optional.ofNullable(column);
    }
    return Optional.empty();
  }

  @Override
  public Optional<ForeignKey> lookupForeignKey(final String name) {
    if (foreignKey.getName().equals(name)) {
      return Optional.ofNullable(foreignKey);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Index> lookupIndex(final String name) {
    throw new NotLoadedException(this);
  }

  @Override
  public Optional<? extends Privilege<Table>> lookupPrivilege(final String name) {
    throw new NotLoadedException(this);
  }

  @Override
  public Optional<TableConstraint> lookupTableConstraint(final String name) {
    throw new NotLoadedException(this);
  }

  @Override
  public Optional<Trigger> lookupTrigger(final String name) {
    throw new NotLoadedException(this);
  }

  void addColumn(final Column column) {
    this.column = column;
  }

  void addForeignKey(final ForeignKey foreignKey) {
    this.foreignKey = foreignKey;
  }
}
