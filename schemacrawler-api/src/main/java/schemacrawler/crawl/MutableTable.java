/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static schemacrawler.utility.NamedObjectSort.alphabetical;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.TableType;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.WeakAssociation;

class MutableTable extends AbstractDatabaseObject implements Table {

  private enum TableAssociationType {
    all,
    exported,
    imported
  }

  private static final long serialVersionUID = 3257290248802284852L;

  private final NamedObjectList<MutableColumn> columns = new NamedObjectList<>();
  private final NamedObjectList<TableConstraint> constraints = new NamedObjectList<>();
  private final NamedObjectList<MutableForeignKey> foreignKeys = new NamedObjectList<>();
  private final NamedObjectList<MutableWeakAssociation> weakAssociations = new NamedObjectList<>();
  private final NamedObjectList<MutableColumn> hiddenColumns = new NamedObjectList<>();
  private final NamedObjectList<MutablePrimaryKey> alternateKeys = new NamedObjectList<>();
  private final NamedObjectList<MutableIndex> indexes = new NamedObjectList<>();
  private final NamedObjectList<MutablePrivilege<Table>> privileges = new NamedObjectList<>();
  private final NamedObjectList<MutableTrigger> triggers = new NamedObjectList<>();
  private final Set<DatabaseObject> referencingObjects = new HashSet<>();
  private MutablePrimaryKey primaryKey;
  private int sortIndex;
  private TableType tableType = TableType.UNKNOWN; // Default value
  private String definition;

  MutableTable(final Schema schema, final String name) {
    super(schema, name);
    definition = "";
  }

  /**
   * {@inheritDoc}
   *
   * <p>NOTE: compareTo is not compatible with equals. equals compares the full name of a database
   * object, but compareTo uses more fields to define a "natural" sorting order. compareTo may
   * return incorrect results until the object is fully built by SchemaCrawler.
   */
  @Override
  public int compareTo(final NamedObject obj) {
    if (obj == null) {
      return -1;
    }

    int comparison = 0;

    if (comparison == 0 && obj instanceof MutableTable) {
      comparison = sortIndex - ((MutableTable) obj).sortIndex;
    }
    if (comparison == 0) {
      comparison = super.compareTo(obj);
    }

    return comparison;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<PrimaryKey> getAlternateKeys() {
    return new HashSet<>(alternateKeys.values());
  }

  /** {@inheritDoc} */
  @Override
  public List<Column> getColumns() {
    return new ArrayList<>(columns.values());
  }

  /** {@inheritDoc} */
  @Override
  public String getDefinition() {
    return definition.toString();
  }

  /** {@inheritDoc} */
  @Override
  public Collection<ForeignKey> getExportedForeignKeys() {
    return getTableReferences(foreignKeys, TableAssociationType.exported);
  }

  /** {@inheritDoc} */
  @Override
  public Collection<ForeignKey> getForeignKeys() {
    return getTableReferences(foreignKeys, TableAssociationType.all);
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Column> getHiddenColumns() {
    return new HashSet<>(hiddenColumns.values());
  }

  @Override
  public Collection<ForeignKey> getImportedForeignKeys() {
    return getTableReferences(foreignKeys, TableAssociationType.imported);
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Index> getIndexes() {
    return new ArrayList<>(indexes.values());
  }

  /** {@inheritDoc} */
  @Override
  public MutablePrimaryKey getPrimaryKey() {
    return primaryKey;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Privilege<Table>> getPrivileges() {
    return new ArrayList<>(privileges.values());
  }

  /** {@inheritDoc} */
  @Override
  public Collection<DatabaseObject> getReferencingObjects() {
    return new HashSet<>(referencingObjects);
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Table> getRelatedTables(final TableRelationshipType tableRelationshipType) {
    final Set<Table> relatedTables = new HashSet<>();
    if (tableRelationshipType != null && tableRelationshipType != TableRelationshipType.none) {
      final List<MutableForeignKey> foreignKeysList = new ArrayList<>(foreignKeys.values());
      for (final ForeignKey foreignKey : foreignKeysList) {
        for (final ColumnReference columnReference : foreignKey) {
          final Table parentTable = columnReference.getPrimaryKeyColumn().getParent();
          final Table childTable = columnReference.getForeignKeyColumn().getParent();
          switch (tableRelationshipType) {
            case parent:
              if (equals(childTable)) {
                relatedTables.add(parentTable);
              }
              break;
            case child:
              if (equals(parentTable)) {
                relatedTables.add(childTable);
              }
              break;
            default:
              break;
          }
        }
      }
    }

    final List<Table> relatedTablesList = new ArrayList<>(relatedTables);
    relatedTablesList.sort(alphabetical);
    return relatedTablesList;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<TableConstraint> getTableConstraints() {
    return new ArrayList<>(constraints.values());
  }

  /** {@inheritDoc} */
  @Override
  public TableType getTableType() {
    return tableType;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Trigger> getTriggers() {
    return new ArrayList<>(triggers.values());
  }

  /** {@inheritDoc} */
  @Override
  public final TableType getType() {
    return getTableType();
  }

  /** {@inheritDoc} */
  @Override
  public Collection<WeakAssociation> getWeakAssociations() {
    return getTableReferences(weakAssociations, TableAssociationType.all);
  }

  @Override
  public final boolean hasDefinition() {
    return !isBlank(definition);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasForeignKeys() {
    return !foreignKeys.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasIndexes() {
    return !indexes.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasPrimaryKey() {
    return getPrimaryKey() != null;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasTriggers() {
    return !triggers.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutablePrimaryKey> lookupAlternateKey(final String name) {
    return alternateKeys.lookup(this, name);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableColumn> lookupColumn(final String name) {
    Optional<MutableColumn> optionalColumn = columns.lookup(this, name);
    if (!optionalColumn.isPresent()) {
      optionalColumn = hiddenColumns.lookup(this, name);
    }
    return optionalColumn;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableForeignKey> lookupForeignKey(final String name) {
    return foreignKeys.lookup(this, name);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableIndex> lookupIndex(final String name) {
    return indexes.lookup(this, name);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutablePrivilege<Table>> lookupPrivilege(final String name) {
    return privileges.lookup(this, name);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<TableConstraint> lookupTableConstraint(final String name) {
    return constraints.lookup(this, name);
  }

  /**
   * Looks up a trigger by name.
   *
   * @param triggerName Trigger name
   * @return Trigger, if found, or null
   */
  @Override
  public Optional<MutableTrigger> lookupTrigger(final String triggerName) {
    return triggers.lookup(this, triggerName);
  }

  final void addAlternateKey(final MutablePrimaryKey alternateKey) {
    alternateKeys.add(alternateKey);
  }

  final void addColumn(final MutableColumn column) {
    columns.add(column);
  }

  final void addForeignKey(final MutableForeignKey foreignKey) {
    foreignKeys.add(foreignKey);
  }

  final void addHiddenColumn(final MutableColumn column) {
    hiddenColumns.add(column);
  }

  final void addIndex(final MutableIndex index) {
    indexes.add(index);
  }

  final void addPrivilege(final MutablePrivilege<Table> privilege) {
    privileges.add(privilege);
  }

  final void addReferencingObjects(final Collection<DatabaseObject> references) {
    if (references == null || references.isEmpty()) {
      return;
    }
    referencingObjects.addAll(references);
  }

  final void addTableConstraint(final TableConstraint tableConstraint) {
    constraints.add(tableConstraint);
  }

  final void addTrigger(final MutableTrigger trigger) {
    triggers.add(trigger);
  }

  final void addWeakAssociation(final MutableWeakAssociation weakAssociation) {
    weakAssociations.add(weakAssociation);
  }

  final NamedObjectList<MutableColumn> getAllColumns() {
    return columns;
  }

  final void removeTableConstraint(final TableConstraint tableConstraint) {
    constraints.remove(tableConstraint);
  }

  final void setDefinition(final String definition) {
    if (!hasDefinition() && !isBlank(definition)) {
      this.definition = definition;
    }
  }

  final void setPrimaryKey(final MutablePrimaryKey primaryKey) {
    if (primaryKey != null) {
      this.primaryKey = primaryKey;
    }
  }

  final void setSortIndex(final int sortIndex) {
    this.sortIndex = sortIndex;
  }

  final void setTableType(final TableType tableType) {
    if (tableType == null) {
      this.tableType = TableType.UNKNOWN;
    } else {
      this.tableType = tableType;
    }
  }

  private <R extends TableReference> Collection<R> getTableReferences(
      final NamedObjectList<? extends R> tableReferences,
      final TableAssociationType tableAssociationType) {

    final List<R> foreignKeysList = new ArrayList<>(tableReferences.values());
    if (tableAssociationType != null && tableAssociationType != TableAssociationType.all) {
      for (final Iterator<R> iterator = foreignKeysList.iterator(); iterator.hasNext(); ) {
        final R foreignKey = iterator.next();

        final boolean isExportedKey = equals(foreignKey.getReferencedTable());
        if (tableAssociationType == TableAssociationType.exported && !isExportedKey) {
          iterator.remove();
          continue;
        }

        final boolean isImportedKey = equals(foreignKey.getDependentTable());
        if (tableAssociationType == TableAssociationType.imported && !isImportedKey) {
          iterator.remove();
          continue;
        }
      }
    }

    // Sort imported keys (constrained columns) first and then exported keys
    // Note: This comparator assumes the all the foreign keys belong to this table - either imported
    // or exported, and no explicit checks for this are done
    final Comparator<R> fkComparator =
        nullsLast(
            ((Comparator<R>)
                    (final R one, final R two) -> {
                      final boolean isOneImportedKey = equals(one.getDependentTable());
                      final boolean isTwoImportedKey = equals(two.getDependentTable());

                      if (isOneImportedKey == isTwoImportedKey) {
                        return 0;
                      }
                      if (isOneImportedKey) {
                        return -1;
                      }
                      return 1;
                    })
                .thenComparing(naturalOrder()));
    Collections.sort(foreignKeysList, fkComparator);

    return foreignKeysList;
  }
}
