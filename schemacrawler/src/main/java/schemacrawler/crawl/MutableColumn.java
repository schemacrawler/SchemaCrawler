/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Privilege;

/**
 * Represents a column in a database table or procedure. Created from
 * metadata returned by a JDBC call.
 * 
 * @author Sualeh Fatehi
 */
class MutableColumn
  extends AbstractColumn
  implements Column
{

  private static final long serialVersionUID = 3834591019449528633L;

  private String defaultValue;
  private boolean isPartOfPrimaryKey;
  private boolean isPartOfUniqueIndex;
  private MutableColumn referencedColumn;
  private final NamedObjectList<MutablePrivilege> privileges = new NamedObjectList<MutablePrivilege>();

  MutableColumn(final DatabaseObject parent, final String name)
  {
    super(parent, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#getDefaultValue()
   */
  public String getDefaultValue()
  {
    return defaultValue;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#getPrivilege(java.lang.String)
   */
  public MutablePrivilege getPrivilege(final String name)
  {
    return privileges.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Column#getPrivileges()
   */
  public Privilege[] getPrivileges()
  {
    return privileges.values().toArray(new Privilege[privileges.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#getReferencedColumn()
   */
  public Column getReferencedColumn()
  {
    return referencedColumn;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#isPartOfForeignKey()
   */
  public boolean isPartOfForeignKey()
  {
    return referencedColumn != null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#isPartOfPrimaryKey()
   */
  public boolean isPartOfPrimaryKey()
  {
    return isPartOfPrimaryKey;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#isPartOfUniqueIndex()
   */
  public boolean isPartOfUniqueIndex()
  {
    return isPartOfUniqueIndex;
  }

  void addPrivilege(final MutablePrivilege privilege)
  {
    privileges.add(privilege);
  }

  void setDefaultValue(final String defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  void setPartOfPrimaryKey(final boolean partOfPrimaryKey)
  {
    isPartOfPrimaryKey = partOfPrimaryKey;
  }

  void setPartOfUniqueIndex(final boolean partOfUniqueIndex)
  {
    isPartOfUniqueIndex = partOfUniqueIndex;
  }

  void setReferencedColumn(final MutableColumn referencedColumn)
  {
    this.referencedColumn = referencedColumn;
  }

}
