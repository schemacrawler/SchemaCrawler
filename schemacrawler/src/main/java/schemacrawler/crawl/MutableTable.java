/*
 * SchemaCrawler http://sourceforge.net/projects/schemacrawler Copyright
 * (c) 2000-2006, Sualeh Fatehi. This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package schemacrawler.crawl;


import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableType;
import schemacrawler.util.NaturalSortComparator;
import schemacrawler.util.SerializableComparator;

/**
 * {@inheritDoc}
 * 
 * @author sfatehi
 */
class MutableTable
  extends AbstractDatabaseObject
  implements Table
{

  private static final long serialVersionUID = 3257290248802284852L;

  private TableType type;
  private PrimaryKey primaryKey;
  private final NamedObjectList columns = new NamedObjectList(
      new NaturalSortComparator());
  private final NamedObjectList foreignKeys = new NamedObjectList(
      new NaturalSortComparator());
  private final NamedObjectList indices = new NamedObjectList(
      new NaturalSortComparator());
  private final NamedObjectList privileges = new NamedObjectList(
      new NaturalSortComparator());

  /**
   * Sets the table type.
   * 
   * @param type
   *          Table type
   */
  void setType(final TableType type)
  {
    if (type == null)
    {
      throw new IllegalArgumentException("Null table type");
    }
    this.type = type;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getType()
   */
  public TableType getType()
  {
    return type;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getPrimaryKey()
   */
  public PrimaryKey getPrimaryKey()
  {
    return primaryKey;
  }

  void setPrimaryKey(final PrimaryKey primaryKey)
  {
    this.primaryKey = primaryKey;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getColumns()
   */
  public Column[] getColumns()
  {
    final List allColumns = columns.getAll();
    return (Column[]) allColumns.toArray(new Column[allColumns.size()]);
  }

  NamedObjectList getColumnsList()
  {
    return columns;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getColumnsListAsString()
   */
  public String getColumnsListAsString()
  {
    String columnsList = "";
    final Column[] columnsArray = getColumns();
    if (columnsArray != null && columnsArray.length > 0)
    {
      final StringBuffer buffer = new StringBuffer();
      for (int i = 0; i < columnsArray.length; i++)
      {
        if (i > 0)
        {
          buffer.append(", ");
        }
        final Column column = columnsArray[i];
        buffer.append(column.getFullName());
      }
      columnsList = buffer.toString();
    }
    return columnsList;
  }

  /**
   * Adds a column.
   * 
   * @param column
   *          Column
   */
  void addColumn(final Column column)
  {
    columns.add(column);
  }

  /**
   * Looks up a column by name.
   * 
   * @param columnName
   *          Column name
   * @return Column, if found, or null
   */
  Column lookupColumn(final String columnName)
  {
    return (Column) columns.lookup(columnName);
  }

  void setColumnComparator(final SerializableComparator comparator)
  {
    columns.setComparator(comparator);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getForeignKeys()
   */
  public ForeignKey[] getForeignKeys()
  {
    final List allForeignKeys = foreignKeys.getAll();
    return (ForeignKey[]) allForeignKeys.toArray(new ForeignKey[allForeignKeys
        .size()]);
  }

  /**
   * Adds a foreign key.
   * 
   * @param foreignKey
   *          Foreign key
   */
  void addForeignKey(final ForeignKey foreignKey)
  {
    foreignKeys.add(foreignKey);
  }

  void setForeignKeyComparator(final SerializableComparator comparator)
  {
    foreignKeys.setComparator(comparator);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getIndices()
   */
  public Index[] getIndices()
  {
    if (primaryKey != null)
    {
      final String primaryKeyName = primaryKey.getName();
      final MutableIndex index = (MutableIndex) indices.remove(primaryKeyName);
      if (index != null)
      {
        setPrimaryKey(MutablePrimaryKey.fromIndex(index));
      }
    }
    final List allIndices = indices.getAll();
    return (Index[]) allIndices.toArray(new Index[allIndices.size()]);
  }

  /**
   * Adds an index.
   * 
   * @param index
   *          Index
   */
  void addIndex(final Index index)
  {
    indices.add(index);
  }

  void setIndexComparator(final SerializableComparator comparator)
  {
    indices.setComparator(comparator);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getPrivileges()
   */
  public Privilege[] getPrivileges()
  {
    final List allPrivileges = privileges.getAll();
    return (Privilege[]) allPrivileges.toArray(new Privilege[allPrivileges
        .size()]);
  }

  /**
   * Adds a privilege.
   * 
   * @param privilege
   *          Privilege
   */
  void addPrivilege(final Privilege privilege)
  {
    privileges.add(privilege);
  }

}
