/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import static schemacrawler.schema.TableRelationshipType.child;
import static schemacrawler.schema.TableRelationshipType.parent;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Represents a table in the database. */
public interface Table
    extends DatabaseObject, TypedObject<TableType>, DefinedObject, ReferencingObject {

  /**
   * Gets the list of all alternate keys of the table.
   *
   * @return Alternate keys of the table.
   */
  Collection<PrimaryKey> getAlternateKeys();

  /**
   * Gets the list of columns in ordinal order.
   *
   * @return Columns of the table
   */
  List<Column> getColumns();

  /**
   * Gets a list of other tables and views, routines and synonyms that use or reference this table.
   *
   * @return Collection of using objects.
   */
  Collection<DatabaseObject> getUsedByObjects();

  /**
   * Gets child tables which have a foreign key from this table.
   *
   * @return Dependent or child tables.
   */
  default Collection<Table> getDependentTables() {
    return getRelatedTables(child);
  }

  /**
   * Gets the list of exported foreign keys. That is, only those whose primary key is referenced in
   * another table.
   *
   * @return Exported foreign keys of the table.
   */
  Collection<ForeignKey> getExportedForeignKeys();

  /**
   * Gets the list of all foreign keys of the table, including imported and exported foreign keys.
   *
   * @return Foreign keys of the table.
   */
  Collection<ForeignKey> getForeignKeys();

  /**
   * Gets hidden columns.
   *
   * @return Columns of the table
   */
  Collection<Column> getHiddenColumns();

  /**
   * Gets the list of imported foreign keys. That is, only those that reference a primary key
   * another table.
   *
   * @return Imported foreign keys of the table.
   */
  Collection<ForeignKey> getImportedForeignKeys();

  /**
   * Gets the list of indexes.
   *
   * @return Indexes of the table.
   */
  Collection<Index> getIndexes();

  /**
   * Gets the primary key.
   *
   * @return Primary key
   */
  PrimaryKey getPrimaryKey();

  /**
   * Gets the list of privileges.
   *
   * @return Privileges for the table.
   */
  Collection<Privilege<Table>> getPrivileges();

  /**
   * Gets parent tables to which this table has a foreign key.
   *
   * @return Referenced or parent tables.
   */
  @Override
  default Collection<Table> getReferencedObjects() {
    return getRelatedTables(parent);
  }

  /**
   * Gets parent tables to which this table has a foreign key.
   *
   * @return Referenced or parent tables.
   */
  default Collection<Table> getReferencedTables() {
    return getRelatedTables(parent);
  }

  /**
   * Gets the tables related to this one, based on the specified relationship type. Child tables are
   * those who have a foreign key from this table. Parent tables are those to which this table has a
   * foreign key.
   *
   * @param tableRelationshipType Table relationship type
   * @return Related tables.
   */
  Collection<Table> getRelatedTables(final TableRelationshipType tableRelationshipType);

  /**
   * Gets the constraints for the table.
   *
   * @return Constraints for the table
   */
  Collection<TableConstraint> getTableConstraints();

  /**
   * Gets the table type.
   *
   * @return Table type.
   */
  TableType getTableType();

  /**
   * Gets the list of triggers.
   *
   * @return Triggers for the table.
   */
  Collection<Trigger> getTriggers();

  /**
   * Gets the list of weak associations.
   *
   * @return Weak associations of the table.
   */
  Collection<WeakAssociation> getWeakAssociations();

  /**
   * Checks if the table has any foreign keys, whether imported or exported.
   *
   * @return True if the table has a foreign keys.
   */
  boolean hasForeignKeys();

  /**
   * Checks if the table has any indexes.
   *
   * @return True if the table has an index.
   */
  boolean hasIndexes();

  /**
   * Checks if the table has a primary key.
   *
   * @return True if the table has a primary key.
   */
  boolean hasPrimaryKey();

  /**
   * Checks if the table has any indexes.
   *
   * @return True if the table has an index.
   */
  boolean hasTriggers();

  /**
   * Gets an alternate key by unqualified name.
   *
   * @param name Name
   * @return Alternate key.
   */
  <A extends PrimaryKey> Optional<A> lookupAlternateKey(String name);

  /**
   * Gets a column by unqualified name.
   *
   * @param name Unqualified name
   * @return Column.
   */
  <C extends Column> Optional<C> lookupColumn(String name);

  /**
   * Gets a foreign key by name.
   *
   * @param name Name
   * @return Foreign key.
   */
  <F extends ForeignKey> Optional<F> lookupForeignKey(String name);

  /**
   * Gets an index by unqualified name.
   *
   * @param name Name
   * @return Index.
   */
  <I extends Index> Optional<I> lookupIndex(String name);

  /**
   * Gets a privilege by unqualified name.
   *
   * @param name Name
   * @return Privilege.
   */
  <P extends Privilege<Table>> Optional<P> lookupPrivilege(String name);

  /**
   * Gets a table constraint by unqualified name.
   *
   * @param name Name
   * @return Table constraint.
   */
  <C extends TableConstraint> Optional<C> lookupTableConstraint(String name);

  /**
   * Gets a trigger by unqualified name.
   *
   * @param name Name
   * @return Trigger.
   */
  <T extends Trigger> Optional<T> lookupTrigger(String name);
}
