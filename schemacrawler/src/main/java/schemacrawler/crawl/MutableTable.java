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

package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.Index;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableAssociationType;
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

  private TableType type = TableType.unknown;// Default value
  private MutablePrimaryKey primaryKey;
  private final NamedObjectList<MutableColumn> columns = new NamedObjectList<MutableColumn>();
  private final NamedObjectList<MutableForeignKey> foreignKeys = new NamedObjectList<MutableForeignKey>();
  private final NamedObjectList<MutableIndex> indices = new NamedObjectList<MutableIndex>();
  private final NamedObjectList<MutableCheckConstraint> checkConstraints = new NamedObjectList<MutableCheckConstraint>();
  private final NamedObjectList<MutableTrigger> triggers = new NamedObjectList<MutableTrigger>();
  private final NamedObjectList<MutablePrivilege> privileges = new NamedObjectList<MutablePrivilege>();
  private final Set<MutableColumnMap> weakAssociations = new LinkedHashSet<MutableColumnMap>();

  MutableTable(final Schema schema, final String name)
  {
    super(schema, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getCheckConstraints()
   */
  public CheckConstraint[] getCheckConstraints()
  {
    return checkConstraints.values()
      .toArray(new CheckConstraint[checkConstraints.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getColumn(java.lang.String)
   */
  public MutableColumn getColumn(final String name)
  {
    return columns.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getColumns()
   */
  public Column[] getColumns()
  {
    return columns.values().toArray(new Column[columns.size()]);
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
      final StringBuilder buffer = new StringBuilder();
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
  public MutableForeignKey getForeignKey(final String name)
  {
    return foreignKeys.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getForeignKeys()
   */
  public ForeignKey[] getForeignKeys()
  {
    return getForeignKeys(TableAssociationType.all);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getForeignKeys(schemacrawler.schema.TableAssociationType)
   */
  public ForeignKey[] getForeignKeys(final TableAssociationType tableAssociationType)
  {
    final List<MutableForeignKey> foreignKeysList = new ArrayList<MutableForeignKey>(foreignKeys
      .values());
    for (final Iterator<MutableForeignKey> iterator = foreignKeysList
      .iterator(); iterator.hasNext();)
    {
      if (tableAssociationType != null)
      {
        final MutableForeignKey mutableForeignKey = iterator.next();
        final ForeignKeyColumnMap[] columnPairs = mutableForeignKey
          .getColumnPairs();
        boolean isExportedKey = false;
        boolean isImportedKey = false;
        for (final ForeignKeyColumnMap columnPair: columnPairs)
        {
          if (columnPair.getPrimaryKeyColumn().getParent().equals(this))
          {
            isExportedKey = true;
          }
          if (columnPair.getForeignKeyColumn().getParent().equals(this))
          {
            isImportedKey = true;
          }
        }
        switch (tableAssociationType)
        {
          case exported:
            if (!isExportedKey)
            {
              iterator.remove();
            }
            break;
          case imported:
            if (!isImportedKey)
            {
              iterator.remove();
            }
            break;
          default:
            break;
        }
      }
    }
    return foreignKeysList.toArray(new ForeignKey[foreignKeysList.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getIndex(java.lang.String)
   */
  public MutableIndex getIndex(final String name)
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
      final MutableIndex index = indices.lookup(this, primaryKeyName);
      if (index != null)
      {
        indices.remove(index);
        setPrimaryKey(new MutablePrimaryKey(index));
      }
    }
    return indices.values().toArray(new Index[indices.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getPrimaryKey()
   */
  public MutablePrimaryKey getPrimaryKey()
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
    return privileges.values().toArray(new Privilege[privileges.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getTrigger(java.lang.String)
   */
  public MutableTrigger getTrigger(final String name)
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
    return triggers.values().toArray(new Trigger[triggers.size()]);
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

  public ColumnMap[] getWeakAssociations()
  {
    return getWeakAssociations(TableAssociationType.all);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getWeakAssociations(schemacrawler.schema.TableAssociationType)
   */
  public ColumnMap[] getWeakAssociations(final TableAssociationType tableAssociationType)
  {
    final List<MutableColumnMap> weakAssociationsList = new ArrayList<MutableColumnMap>(weakAssociations);
    if (tableAssociationType != null)
    {
      for (final Iterator<MutableColumnMap> iterator = weakAssociationsList
        .iterator(); iterator.hasNext();)
      {
        final ColumnMap weakAssociation = iterator.next();
        switch (tableAssociationType)
        {
          case exported:
            if (!weakAssociation.getPrimaryKeyColumn().getParent().equals(this))
            {
              iterator.remove();
            }
            break;
          case imported:
            if (!weakAssociation.getForeignKeyColumn().getParent().equals(this))
            {
              iterator.remove();
            }
            break;
          default:
            break;
        }
      }
    }
    return weakAssociationsList.toArray(new ColumnMap[weakAssociationsList
      .size()]);
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

  void addWeakAssociation(final MutableColumnMap columnMap)
  {
    if (columnMap != null)
    {
      weakAssociations.add(columnMap);
    }
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

  void setPrimaryKey(final MutablePrimaryKey primaryKey)
  {
    this.primaryKey = primaryKey;
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
