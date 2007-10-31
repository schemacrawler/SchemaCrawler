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

package schemacrawler.crawl;


import schemacrawler.crawl.NamedObjectList.NamedObjectSort;
import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableType;
import schemacrawler.schema.Trigger;

/**
 * {@inheritDoc}
 * 
 * @author Sualeh Fatehi
 */
class MutableTable
  extends AbstractDatabaseObject
  implements Table
{

  private static final long serialVersionUID = 3257290248802284852L;

  private TableType type;
  private PrimaryKey primaryKey;
  private final NamedObjectList<MutableColumn> columns = new NamedObjectList<MutableColumn>(NamedObjectSort.natural);
  private final NamedObjectList<MutableForeignKey> foreignKeys = new NamedObjectList<MutableForeignKey>(NamedObjectSort.natural);
  private final NamedObjectList<MutableIndex> indices = new NamedObjectList<MutableIndex>(NamedObjectSort.natural);
  private final NamedObjectList<MutableCheckConstraint> checkConstraints = new NamedObjectList<MutableCheckConstraint>(NamedObjectSort.natural);
  private final NamedObjectList<MutableTrigger> triggers = new NamedObjectList<MutableTrigger>(NamedObjectSort.natural);
  private final NamedObjectList<MutablePrivilege> privileges = new NamedObjectList<MutablePrivilege>(NamedObjectSort.natural);

  MutableTable(final String catalogName,
               final String schemaName,
               final String name)
  {
    super(catalogName, schemaName, name);
    // Default values
    type = TableType.unknown;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getCheckConstraints()
   */
  public CheckConstraint[] getCheckConstraints()
  {
    return checkConstraints.getAll()
      .toArray(new CheckConstraint[checkConstraints.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getColumn(java.lang.String)
   */
  public Column getColumn(final String name)
  {
    return columns.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getColumns()
   */
  public Column[] getColumns()
  {
    return columns.getAll().toArray(new Column[columns.size()]);
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
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getForeignKey(java.lang.String)
   */
  public ForeignKey getForeignKey(final String name)
  {
    return foreignKeys.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getForeignKeys()
   */
  public ForeignKey[] getForeignKeys()
  {
    return foreignKeys.getAll().toArray(new ForeignKey[foreignKeys.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getIndex(java.lang.String)
   */
  public Index getIndex(final String name)
  {
    return indices.lookup(name);
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
      final MutableIndex index = indices.remove(primaryKeyName);
      if (index != null)
      {
        setPrimaryKey(MutablePrimaryKey.fromIndex(index));
      }
    }
    return indices.getAll().toArray(new Index[indices.size()]);
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

  /**
   * {@inheritDoc}
   * 
   * @see Table#getPrivileges()
   */
  public Privilege[] getPrivileges()
  {
    return privileges.getAll().toArray(new Privilege[privileges.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getTrigger(java.lang.String)
   */
  public Trigger getTrigger(final String name)
  {
    return triggers.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getTriggers()
   */
  public Trigger[] getTriggers()
  {
    return triggers.getAll().toArray(new Trigger[triggers.size()]);
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
   * Adds an check constraints.
   * 
   * @param checkConstraints
   *        Check constraints
   */
  void addCheckConstraint(final MutableCheckConstraint checkConstraint)
  {
    checkConstraints.add(checkConstraint);
  }

  /**
   * Adds a column.
   * 
   * @param column
   *        Column
   */
  void addColumn(final MutableColumn column)
  {
    columns.add(column);
  }

  /**
   * Adds a foreign key.
   * 
   * @param foreignKey
   *        Foreign key
   */
  void addForeignKey(final MutableForeignKey foreignKey)
  {
    foreignKeys.add(foreignKey);
  }

  /**
   * Adds an index.
   * 
   * @param index
   *        Index
   */
  void addIndex(final MutableIndex index)
  {
    indices.add(index);
  }

  /**
   * Adds a privilege.
   * 
   * @param privilege
   *        Privilege
   */
  void addPrivilege(final MutablePrivilege privilege)
  {
    privileges.add(privilege);
  }

  /**
   * Adds an trigger.
   * 
   * @param trigger
   *        Trigger
   */
  void addTrigger(final MutableTrigger trigger)
  {
    triggers.add(trigger);
  }

  NamedObjectList<MutableColumn> getColumnsList()
  {
    return columns;
  }

  /**
   * Looks up a column by name.
   * 
   * @param columnName
   *        Column name
   * @return Column, if found, or null
   */
  Column lookupColumn(final String columnName)
  {
    return columns.lookup(columnName);
  }

  void setCheckConstraintComparator(final NamedObjectSort comparator)
  {
    checkConstraints.setSortOrder(comparator);
  }

  void setColumnComparator(final NamedObjectSort comparator)
  {
    columns.setSortOrder(comparator);
  }

  void setForeignKeyComparator(final NamedObjectSort comparator)
  {
    foreignKeys.setSortOrder(comparator);
  }

  void setIndexComparator(final NamedObjectSort comparator)
  {
    indices.setSortOrder(comparator);
  }

  void setPrimaryKey(final PrimaryKey primaryKey)
  {
    this.primaryKey = primaryKey;
  }

  void setTriggerComparator(final NamedObjectSort comparator)
  {
    triggers.setSortOrder(comparator);
  }

  /**
   * Sets the table type.
   * 
   * @param type
   *        Table type
   */
  void setType(final TableType type)
  {
    if (type == null)
    {
      throw new IllegalArgumentException("Null table type");
    }
    this.type = type;
  }

}
