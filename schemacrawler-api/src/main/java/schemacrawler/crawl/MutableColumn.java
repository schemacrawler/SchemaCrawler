/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import schemacrawler.schema.Column;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Table;

/**
 * Represents a column in a database table or routine. Created from
 * metadata returned by a JDBC call.
 *
 * @author Sualeh Fatehi
 */
class MutableColumn
  extends AbstractColumn<Table>
  implements Column
{

  private static final long serialVersionUID = 3834591019449528633L;

  private String defaultValue;
  private boolean isAutoIncremented;
  private boolean isGenerated;
  private boolean isPartOfPrimaryKey;
  private boolean isPartOfUniqueIndex;
  private boolean isPartOfIndex;
  private Column referencedColumn;
  private final NamedObjectList<MutablePrivilege<Column>> privileges = new NamedObjectList<>();

  MutableColumn(final Table parent, final String name)
  {
    super(new TableReference(parent), name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#getDefaultValue()
   */
  @Override
  public String getDefaultValue()
  {
    return defaultValue;
  }

  /**
   * {@inheritDoc}
   *
   * @see Column#getPrivileges()
   */
  @Override
  public Collection<Privilege<Column>> getPrivileges()
  {
    return new ArrayList<Privilege<Column>>(privileges.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#getReferencedColumn()
   */
  @Override
  public Column getReferencedColumn()
  {
    return referencedColumn;
  }

  /**
   * @return the isAutoIncremented
   */
  @Override
  public boolean isAutoIncremented()
  {
    return isAutoIncremented;
  }

  /**
   * @return the isGenerated
   */
  @Override
  public boolean isGenerated()
  {
    return isGenerated;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfForeignKey()
   */
  @Override
  public boolean isPartOfForeignKey()
  {
    return referencedColumn != null;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfIndex()
   */
  @Override
  public boolean isPartOfIndex()
  {
    return isPartOfIndex;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfPrimaryKey()
   */
  @Override
  public boolean isPartOfPrimaryKey()
  {
    return isPartOfPrimaryKey;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfUniqueIndex()
   */
  @Override
  public boolean isPartOfUniqueIndex()
  {
    return isPartOfUniqueIndex;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#lookupPrivilege(java.lang.String)
   */
  @Override
  public Optional<MutablePrivilege<Column>> lookupPrivilege(final String name)
  {
    return privileges.lookup(this, name);
  }

  void addPrivilege(final MutablePrivilege<Column> privilege)
  {
    privileges.add(privilege);
  }

  void markAsPartOfIndex()
  {
    isPartOfIndex = true;
  }

  void markAsPartOfPrimaryKey()
  {
    isPartOfPrimaryKey = true;
  }

  void markAsPartOfUniqueIndex()
  {
    isPartOfUniqueIndex = true;
  }

  /**
   * @param isAutoIncremented
   *        the isAutoIncremented to set
   */
  void setAutoIncremented(final boolean isAutoIncremented)
  {
    this.isAutoIncremented = isAutoIncremented;
  }

  void setDefaultValue(final String defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  /**
   * @param isGenerated
   *        the isGenerated to set
   */
  void setGenerated(final boolean isGenerated)
  {
    this.isGenerated = isGenerated;
  }

  void setReferencedColumn(final Column referencedColumn)
  {
    this.referencedColumn = referencedColumn;
  }

}
