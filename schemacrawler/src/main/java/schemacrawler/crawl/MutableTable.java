/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


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
 * Represents a table in the database.
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

  void addCheckConstraint(final MutableCheckConstraint checkConstraint)
  {
    checkConstraints.add(checkConstraint);
  }

  void addColumn(final MutableColumn column)
  {
    columns.add(column);
  }

  void addForeignKey(final MutableForeignKey foreignKey)
  {
    foreignKeys.add(foreignKey);
  }

  void addIndex(final MutableIndex index)
  {
    indices.add(index);
  }

  void addPrivilege(final MutablePrivilege privilege)
  {
    privileges.add(privilege);
  }

  void addTrigger(final MutableTrigger trigger)
  {
    triggers.add(trigger);
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
    return lookupColumn(name);
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

  NamedObjectList<MutableColumn> getColumnsList()
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
        buffer.append(column.getName());
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
    return foreignKeys.lookup(this, name);
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
    return indices.lookup(this, name);
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
        setPrimaryKey(new MutablePrimaryKey(index));
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
    return lookupTrigger(name);
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
   * Looks up a column by name.
   * 
   * @param columnName
   *        Column name
   * @return Column, if found, or null
   */
  MutableColumn lookupColumn(final String columnName)
  {
    return columns.lookup(this, columnName);
  }

  /**
   * Looks up a trigger by name.
   * 
   * @param triggerName
   *        Trigger name
   * @return Trigger, if found, or null
   */
  MutableTrigger lookupTrigger(final String triggerName)
  {
    return triggers.lookup(this, triggerName);
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

  void setType(final TableType type)
  {
    if (type == null)
    {
      throw new IllegalArgumentException("Null table type");
    }
    this.type = type;
  }

}
