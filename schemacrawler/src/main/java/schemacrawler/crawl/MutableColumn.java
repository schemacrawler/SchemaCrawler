/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

package schemacrawler.crawl;


import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Privilege;
import schemacrawler.util.NaturalSortComparator;

/**
 * Represents a column in a database table. Created from metadata
 * returned by a JDBC call.
 * 
 * @author sfatehi
 * @version 0.1
 */
final class MutableColumn
  extends AbstractColumn
  implements Column
{

  private static final long serialVersionUID = 3834591019449528633L;

  private String defaultValue;
  private boolean isPartOfPrimaryKey;
  private boolean isPartOfUniqueIndex;
  private final NamedObjectList privileges = new NamedObjectList(new NaturalSortComparator());

  MutableColumn(final String name, final NamedObject parent)
  {
    super(name, parent);
  }

  /**
   * Gets the column's default value.
   * 
   * @return Default value
   */
  public String getDefaultValue()
  {
    return defaultValue;
  }

  /**
   * Setter for property default value.
   * 
   * @param defaultValue
   *        New value of property default value.
   */
  void setDefaultValue(final String defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  /**
   * Whether the column is a part of the primary key.
   * 
   * @return Whether the column is a part of the primary key
   */
  public boolean isPartOfPrimaryKey()
  {
    return isPartOfPrimaryKey;
  }

  /**
   * Sets true if this column is a part of primary key.
   * 
   * @param partOfPrimaryKey
   *        Is the column a part of primary key
   */
  void setPartOfPrimaryKey(final boolean partOfPrimaryKey)
  {
    isPartOfPrimaryKey = partOfPrimaryKey;
  }

  /**
   * If the column has unique index.
   * 
   * @return If the column has a unique index.
   */
  public boolean isPartOfUniqueIndex()
  {
    return isPartOfUniqueIndex;
  }

  /**
   * Sets true if this column is a unique index.
   * 
   * @param partOfUniqueIndex
   *        Is the column a part of a unique index
   */
  void setPartOfUniqueIndex(final boolean partOfUniqueIndex)
  {
    isPartOfUniqueIndex = partOfUniqueIndex;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Column#getPrivileges()
   */
  public Privilege[] getPrivileges()
  {
    final List allPrivileges = privileges.getAll();
    return (Privilege[]) allPrivileges.toArray(new Privilege[allPrivileges
      .size()]);
  }

  /**
   * Adds a privilege to the collection.
   * 
   * @param privilege
   *        Privilege
   */
  void addPrivilege(final Privilege privilege)
  {
    privileges.add(privilege);
  }

}
