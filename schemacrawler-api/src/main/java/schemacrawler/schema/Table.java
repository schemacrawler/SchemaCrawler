/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.schema;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Represents a table in the database.
 *
 * @author Sualeh Fatehi
 */
public interface Table
  extends DatabaseObject, TypedObject<TableType>, DefinedObject
{

  /**
   * Gets the list of columns in ordinal order.
   *
   * @return Columns of the table
   */
  List<Column> getColumns();

  /**
   * Gets the list of exported foreign keys. That is, only those whose
   * primary key is referenced in another table.
   *
   * @return Exported foreign keys of the table.
   */
  Collection<ForeignKey> getExportedForeignKeys();

  /**
   * Gets the list of foreign keys. Same as calling
   * getForeignKeys(TableAssociationType.all).
   *
   * @return Foreign keys of the table.
   */
  Collection<ForeignKey> getForeignKeys();

  /**
   * Gets the list of imported foreign keys. That is, only those that
   * reference a primary key another table.
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
   * Gets the tables related to this one, based on the specified
   * relationship type. Child tables are those who have a foreign key
   * from this table. Parent tables are those to which this table has a
   * foreign key.
   *
   * @param tableRelationshipType
   *        Table relationship type
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
   * Gets a column by unqualified name.
   *
   * @param name
   *        Unqualified name
   * @return Column.
   */
  Optional<? extends Column> lookupColumn(String name);

  /**
   * Gets a foreign key by name.
   *
   * @param name
   *        Name
   * @return Foreign key.
   */
  Optional<? extends ForeignKey> lookupForeignKey(String name);

  /**
   * Gets an index by unqualified name.
   *
   * @param name
   *        Name
   * @return Index.
   */
  Optional<? extends Index> lookupIndex(String name);

  /**
   * Gets a privilege by unqualified name.
   *
   * @param name
   *        Name
   * @return Privilege.
   */
  Optional<? extends Privilege<Table>> lookupPrivilege(String name);

  /**
   * Gets a trigger by unqualified name.
   *
   * @param name
   *        Name
   * @return Trigger.
   */
  Optional<? extends Trigger> lookupTrigger(String name);

}
