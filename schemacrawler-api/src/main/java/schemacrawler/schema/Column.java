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


/**
 * Represents a column in a database table or procedure.
 * 
 * @author Sualeh Fatehi
 */
public interface Column
  extends BaseColumn<Table>
{

  /**
   * Gets the default data value for the column.
   * 
   * @return Default data value for the column
   */
  String getDefaultValue();

  /**
   * Gets a privilege by name.
   * 
   * @param name
   *        Name
   * @return Privilege.
   */
  Privilege<Column> getPrivilege(String name);

  /**
   * Gets the list of privileges for the table.
   * 
   * @return Privileges for the table
   */
  Privilege<Column>[] getPrivileges();

  /**
   * Referenced column if this column is part of a foreign key, null
   * otherwise.
   * 
   * @return Referenced column
   */
  Column getReferencedColumn();

  /**
   * True if this column is part of a foreign key.
   * 
   * @return If the column is part of a foreign key
   */
  boolean isPartOfForeignKey();

  /**
   * True if this column is a part of primary key.
   * 
   * @return If the column is a part of primary key
   */
  boolean isPartOfPrimaryKey();

  /**
   * True if this column is a unique index.
   * 
   * @return If the column is a unique index
   */
  boolean isPartOfUniqueIndex();

}
