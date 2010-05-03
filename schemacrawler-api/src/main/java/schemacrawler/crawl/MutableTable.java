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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;
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

  private enum TableAssociationType
  {

    all,
    exported,
    imported;
  }

  private static final long serialVersionUID = 3257290248802284852L;

  private TableType type = TableType.unknown; // Default value
  private MutablePrimaryKey primaryKey;
  private final NamedObjectList<MutableColumn> columns = new NamedObjectList<MutableColumn>();
  private final NamedObjectList<MutableForeignKey> foreignKeys = new NamedObjectList<MutableForeignKey>();
  private final NamedObjectList<MutableIndex> indices = new NamedObjectList<MutableIndex>();
  private final NamedObjectList<MutableCheckConstraint> checkConstraints = new NamedObjectList<MutableCheckConstraint>();
  private final NamedObjectList<MutableTrigger> triggers = new NamedObjectList<MutableTrigger>();
  private final NamedObjectList<MutablePrivilege> privileges = new NamedObjectList<MutablePrivilege>();
  private int sortIndex;

  MutableTable(final Schema schema,
               final String name,
               final String quoteCharacter)
  {
    super(schema, name, quoteCharacter);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    if (obj == null)
    {
      return -1;
    }

    final MutableTable other = (MutableTable) obj;
    int comparison = 0;

    if (comparison == 0)
    {
      comparison = sortIndex - other.sortIndex;
    }
    if (comparison == 0)
    {
      comparison = super.compareTo(other);
    }

    return comparison;
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
    final Column[] columnsArray = getColumns();
    final StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < columnsArray.length; i++)
    {
      final Column column = columnsArray[i];
      if (i > 0)
      {
        buffer.append(", ");
      }
      buffer.append(column.getName());
    }
    return buffer.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getExportedForeignKeys()
   */
  public ForeignKey[] getExportedForeignKeys()
  {
    return getForeignKeys(TableAssociationType.exported);
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

  public ForeignKey[] getImportedForeignKeys()
  {
    return getForeignKeys(TableAssociationType.imported);
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
   * @see schemacrawler.schema.Table#getPrivilege(java.lang.String)
   */
  public MutablePrivilege getPrivilege(final String name)
  {
    return privileges.lookup(this, name);
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
   * @see schemacrawler.schema.Table#getRelatedTables(schemacrawler.schema.TableRelationshipType)
   */
  public Table[] getRelatedTables(final TableRelationshipType tableRelationshipType)
  {
    final Set<MutableTable> relatedTables = new HashSet<MutableTable>();
    if (tableRelationshipType != null
        && tableRelationshipType != TableRelationshipType.none)
    {
      final List<MutableForeignKey> foreignKeysList = new ArrayList<MutableForeignKey>(foreignKeys
        .values());
      for (final MutableForeignKey mutableForeignKey: foreignKeysList)
      {
        for (final ForeignKeyColumnMap columnPair: mutableForeignKey
          .getColumnPairs())
        {
          final MutableTable parentTable = (MutableTable) columnPair
            .getPrimaryKeyColumn().getParent();
          final MutableTable childTable = (MutableTable) columnPair
            .getForeignKeyColumn().getParent();
          switch (tableRelationshipType)
          {
            case parent:
              if (equals(childTable))
              {
                relatedTables.add(parentTable);
              }
              break;
            case child:
              if (equals(parentTable))
              {
                relatedTables.add(childTable);
              }
              break;
            default:
              break;
          }
        }
      }
    }
    return relatedTables.toArray(new Table[relatedTables.size()]);
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

  int getSortIndex()
  {
    return sortIndex;
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

  void replacePrimaryKey()
  {
    if (primaryKey == null)
    {
      return;
    }

    final String primaryKeyName = primaryKey.getName();
    final MutableIndex index = indices.lookup(this, primaryKeyName);
    if (index != null)
    {
      boolean indexHasPkColumns = false;
      final IndexColumn[] pkColumns = primaryKey.getColumns();
      final IndexColumn[] indexColumns = index.getColumns();
      if (pkColumns.length == indexColumns.length)
      {
        for (int i = 0; i < indexColumns.length; i++)
        {
          if (!pkColumns[i].equals(indexColumns[i]))
          {
            break;
          }
        }
        indexHasPkColumns = true;
      }
      if (indexHasPkColumns)
      {
        indices.remove(index);
        setPrimaryKey(new MutablePrimaryKey(index));
      }
    }
  }

  void setColumnsSortOrder(final NamedObjectSort sort)
  {
    columns.setSortOrder(sort);
  }

  void setForeignKeysSortOrder(final NamedObjectSort sort)
  {
    foreignKeys.setSortOrder(sort);
  }

  void setIndicesSortOrder(final NamedObjectSort sort)
  {
    indices.setSortOrder(sort);
  }

  void setPrimaryKey(final MutablePrimaryKey primaryKey)
  {
    this.primaryKey = primaryKey;
  }

  void setSortIndex(final int sortIndex)
  {
    this.sortIndex = sortIndex;
  }

  void setType(final TableType type)
  {
    if (type == null)
    {
      throw new IllegalArgumentException("Null table type");
    }
    this.type = type;
  }

  private ForeignKey[] getForeignKeys(final TableAssociationType tableAssociationType)
  {
    final List<MutableForeignKey> foreignKeysList = new ArrayList<MutableForeignKey>(foreignKeys
      .values());
    if (tableAssociationType != null
        && tableAssociationType != TableAssociationType.all)
    {
      for (final Iterator<MutableForeignKey> iterator = foreignKeysList
        .iterator(); iterator.hasNext();)
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

}
