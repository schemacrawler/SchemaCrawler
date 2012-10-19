/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.schema;


import java.util.Collection;
import java.util.List;

/**
 * Represents a table in the database.
 * 
 * @author Sualeh Fatehi
 */
public interface Table
  extends DatabaseObject
{

  /**
   * Gets the list of check constraints.
   * 
   * @return Check constraints for the table
   */
  Collection<CheckConstraint> getCheckConstraints();

  /**
   * Gets a column by name.
   * 
   * @param name
   *        Name
   * @return Column.
   */
  Column getColumn(String name);

  /**
   * Gets the list of columns in ordinal order.
   * 
   * @return Columns of the table
   */
  List<Column> getColumns();

  /**
   * Gets a comma-separated list of columns.
   * 
   * @return Comma-separated list of columns
   */
  String getColumnsListAsString();

  /**
   * Gets the list of exported foreign keys. That is, only those whose
   * primary key is referenced in another table.
   * 
   * @return Exported foreign keys of the table.
   */
  Collection<ForeignKey> getExportedForeignKeys();

  /**
   * Gets a foreign key by name.
   * 
   * @param name
   *        Name
   * @return ForeignKey.
   */
  ForeignKey getForeignKey(String name);

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
   * Gets an index by name.
   * 
   * @param name
   *        Name
   * @return Index.
   */
  Index getIndex(String name);

  /**
   * Gets the list of indices.
   * 
   * @return Indices of the table.
   */
  Collection<Index> getIndices();

  /**
   * Gets the primary key.
   * 
   * @return Primary key
   */
  PrimaryKey getPrimaryKey();

  /**
   * Gets a privilege by name.
   * 
   * @param name
   *        Name
   * @return Privilege.
   */
  Privilege<Table> getPrivilege(String name);

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
   * Gets the table type.
   * 
   * @return Table type.
   */
  TableType getTableType();

  /**
   * Gets a trigger by name.
   * 
   * @param name
   *        Name
   * @return Trigger.
   */
  Trigger getTrigger(String name);

  /**
   * Gets the list of triggers.
   * 
   * @return Triggers for the table.
   */
  Collection<Trigger> getTriggers();

}
