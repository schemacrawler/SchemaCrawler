/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
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
  private final NamedObjectList<MutablePrivilege<Table>> privileges = new NamedObjectList<MutablePrivilege<Table>>();
  private int sortIndex;

  MutableTable(final Schema schema, final String name)
  {
    super(schema, name);
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
  @Override
  public Collection<CheckConstraint> getCheckConstraints()
  {
    return new ArrayList<CheckConstraint>(checkConstraints.values());
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getColumn(java.lang.String)
   */
  @Override
  public MutableColumn getColumn(final String name)
  {
    return columns.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getColumns()
   */
  @Override
  public List<Column> getColumns()
  {
    return new ArrayList<Column>(columns.values());
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getColumnsListAsString()
   */
  @Override
  public String getColumnsListAsString()
  {
    final List<Column> columnsArray = getColumns();
    final StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < columnsArray.size(); i++)
    {
      final Column column = columnsArray.get(i);
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
  @Override
  public Collection<ForeignKey> getExportedForeignKeys()
  {
    return getForeignKeys(TableAssociationType.exported);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getForeignKey(java.lang.String)
   */
  @Override
  public MutableForeignKey getForeignKey(final String name)
  {
    return foreignKeys.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getForeignKeys()
   */
  @Override
  public Collection<ForeignKey> getForeignKeys()
  {
    return getForeignKeys(TableAssociationType.all);
  }

  @Override
  public Collection<ForeignKey> getImportedForeignKeys()
  {
    return getForeignKeys(TableAssociationType.imported);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getIndex(java.lang.String)
   */
  @Override
  public MutableIndex getIndex(final String name)
  {
    return indices.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getIndices()
   */
  @Override
  public Collection<Index> getIndices()
  {
    final List<Index> values = new ArrayList<Index>(indices.values());
    return values;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getPrimaryKey()
   */
  @Override
  public MutablePrimaryKey getPrimaryKey()
  {
    return primaryKey;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getPrivilege(java.lang.String)
   */
  @Override
  public MutablePrivilege<Table> getPrivilege(final String name)
  {
    return privileges.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getPrivileges()
   */
  @Override
  public Collection<Privilege<Table>> getPrivileges()
  {
    return new ArrayList<Privilege<Table>>(privileges.values());
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getRelatedTables(schemacrawler.schema.TableRelationshipType)
   */
  @Override
  public Collection<Table> getRelatedTables(final TableRelationshipType tableRelationshipType)
  {
    final Set<Table> relatedTables = new HashSet<Table>();
    if (tableRelationshipType != null
        && tableRelationshipType != TableRelationshipType.none)
    {
      final List<MutableForeignKey> foreignKeysList = new ArrayList<MutableForeignKey>(foreignKeys
        .values());
      for (final MutableForeignKey mutableForeignKey: foreignKeysList)
      {
        for (final ForeignKeyColumnReference columnReference: mutableForeignKey
          .getColumnReferences())
        {
          final MutableTable parentTable = (MutableTable) columnReference
            .getPrimaryKeyColumn().getParent();
          final MutableTable childTable = (MutableTable) columnReference
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
    return relatedTables;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Table#getTrigger(java.lang.String)
   */
  @Override
  public MutableTrigger getTrigger(final String name)
  {
    return lookupTrigger(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getTriggers()
   */
  @Override
  public Collection<Trigger> getTriggers()
  {
    return new ArrayList<Trigger>(triggers.values());
  }

  /**
   * {@inheritDoc}
   * 
   * @see Table#getType()
   */
  @Override
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

  void addPrivilege(final MutablePrivilege<Table> privilege)
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
      final List<IndexColumn> pkColumns = primaryKey.getColumns();
      final List<IndexColumn> indexColumns = index.getColumns();
      if (pkColumns.size() == indexColumns.size())
      {
        for (int i = 0; i < indexColumns.size(); i++)
        {
          if (!pkColumns.get(i).equals(indexColumns.get(i)))
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

  private Collection<ForeignKey> getForeignKeys(final TableAssociationType tableAssociationType)
  {
    final List<ForeignKey> foreignKeysList = new ArrayList<ForeignKey>(foreignKeys
      .values());
    if (tableAssociationType != null
        && tableAssociationType != TableAssociationType.all)
    {
      for (final Iterator<ForeignKey> iterator = foreignKeysList.iterator(); iterator
        .hasNext();)
      {
        final ForeignKey mutableForeignKey = iterator.next();
        boolean isExportedKey = false;
        boolean isImportedKey = false;
        for (final ForeignKeyColumnReference columnReference: mutableForeignKey
          .getColumnReferences())
        {
          if (columnReference.getPrimaryKeyColumn().getParent().equals(this))
          {
            isExportedKey = true;
          }
          if (columnReference.getForeignKeyColumn().getParent().equals(this))
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
    return foreignKeysList;
  }

}
