/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
import java.util.Optional;

/**
 * Represents a column in a database table or routine.
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
   * Gets the list of privileges for the table.
   *
   * @return Privileges for the table
   */
  Collection<Privilege<Column>> getPrivileges();

  /**
   * Referenced column if this column is part of a foreign key, null
   * otherwise.
   *
   * @return Referenced column
   */
  Column getReferencedColumn();

  /**
   * True if this column is auto-incremented.
   *
   * @return If the column is auto-incremented
   */
  boolean isAutoIncremented();

  /**
   * True if this column is a generated column.
   *
   * @return If the column is a generated column
   */
  boolean isGenerated();

  /**
   * True if this column is a hidden column.
   *
   * @return If the column is a hidden column
   */
  boolean isHidden();

  /**
   * True if this column is part of a foreign key.
   *
   * @return If the column is part of a foreign key
   */
  boolean isPartOfForeignKey();

  /**
   * True if this column is part of an index.
   *
   * @return If the column is part of an index
   */
  boolean isPartOfIndex();

  /**
   * True if this column is a part of primary key.
   *
   * @return If the column is a part of primary key
   */
  boolean isPartOfPrimaryKey();

  /**
   * True if this column is part of an unique index.
   *
   * @return If the column is part of an unique index
   */
  boolean isPartOfUniqueIndex();

  /**
   * Gets a privilege by unqualified name.
   *
   * @param name
   *        Unqualified name
   * @return Privilege.
   */
  Optional<? extends Privilege<Column>> lookupPrivilege(String name);

}
