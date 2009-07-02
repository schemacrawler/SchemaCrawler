/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
  CheckConstraint[] getCheckConstraints();

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
  Column[] getColumns();

  /**
   * Gets a comma-separated list of columns.
   * 
   * @return Comma-separated list of columns
   */
  String getColumnsListAsString();

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
  ForeignKey[] getForeignKeys();

  /**
   * Gets the list of foreign keys.
   * 
   * @param tableAssociationType
   *        Specifies what kind of foreign keys are to be returned, one
   *        of
   *        <ul>
   *        <li>all</li>
   *        <li>exported, that is, only those whose primary key is
   *        referenced in another table</li>
   *        <li>imported, that is, only those that reference a primary
   *        key another table</li>
   *        </ul>
   * @return Foreign keys of the table.
   */
  ForeignKey[] getForeignKeys(TableAssociationType tableAssociationType);

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
  Index[] getIndices();

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
  Privilege[] getPrivileges();

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
  Trigger[] getTriggers();

  /**
   * Gets the table type.
   * 
   * @return Table type.
   */
  TableType getType();

  /**
   * Weak column associations that are derived by SchemaCrawler from the
   * column names. Same as calling
   * getWeakAssociations(TableAssociationType.all).
   * 
   * @return Weak associations for the table
   */
  ColumnMap[] getWeakAssociations();

  /**
   * Weak column associations that are derived by SchemaCrawler from the
   * column names.
   * 
   * @param tableAssociationType
   *        Specifies what kind of weak associations are to be returned,
   *        one of
   *        <ul>
   *        <li>all</li>
   *        <li>exported, that is, only those whose primary key is
   *        referenced in another table</li>
   *        <li>imported, that is, only those that reference a primary
   *        key another table</li>
   *        </ul>
   * @return Weak associations for the table
   */
  ColumnMap[] getWeakAssociations(TableAssociationType tableAssociationType);

}
