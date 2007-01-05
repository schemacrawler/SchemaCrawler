/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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
 * A table or procedure column.
 * 
 * @author sfatehi
 */
public interface Column
  extends BaseColumn
{

  /**
   * Getter for property default value.
   * 
   * @return Value of property default value.
   */
  String getDefaultValue();

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

  /**
   * List of privileges.
   * 
   * @return Privileges for the table.
   */
  Privilege[] getPrivileges();

}
