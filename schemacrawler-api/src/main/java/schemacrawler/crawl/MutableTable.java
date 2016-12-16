/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Index;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.TableType;
import schemacrawler.schema.Trigger;
import schemacrawler.utility.NamedObjectSort;

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

  private TableType tableType = TableType.UNKNOWN; // Default value
  private MutablePrimaryKey primaryKey;
  private final NamedObjectList<MutableColumn> columns = new NamedObjectList<>();
  private final NamedObjectList<MutableColumn> hiddenColumns = new NamedObjectList<>();
  private final NamedObjectList<MutableForeignKey> foreignKeys = new NamedObjectList<>();
  private final NamedObjectList<MutableIndex> indexes = new NamedObjectList<>();
  private final NamedObjectList<MutableTableConstraint> constraints = new NamedObjectList<>();
  private final NamedObjectList<MutableTrigger> triggers = new NamedObjectList<>();
  private final NamedObjectList<MutablePrivilege<Table>> privileges = new NamedObjectList<>();
  private int sortIndex;
  private final StringBuilder definition;

  MutableTable(final Schema schema, final String name)
  {
    super(schema, name);
    definition = new StringBuilder();
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

    int comparison = 0;

    if (comparison == 0 && obj instanceof MutableTable)
    {
      comparison = sortIndex - ((MutableTable) obj).sortIndex;
    }
    if (comparison == 0)
    {
      comparison = super.compareTo(obj);
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   *
   * @see Table#getColumns()
   */
  @Override
  public List<Column> getColumns()
  {
    return new ArrayList<>(columns.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.View#getDefinition()
   */
  @Override
  public String getDefinition()
  {
    return definition.toString();
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
   * @see schemacrawler.schema.Table#getForeignKeys()
   */
  @Override
  public Collection<ForeignKey> getForeignKeys()
  {
    return getForeignKeys(TableAssociationType.all);
  }

  /**
   * {@inheritDoc}
   *
   * @see Table#getColumns()
   */
  @Override
  public Collection<Column> getHiddenColumns()
  {
    return new HashSet<>(hiddenColumns.values());
  }

  @Override
  public Collection<ForeignKey> getImportedForeignKeys()
  {
    return getForeignKeys(TableAssociationType.imported);
  }

  /**
   * {@inheritDoc}
   *
   * @see Table#getIndexes()
   */
  @Override
  public Collection<Index> getIndexes()
  {
    return new ArrayList<>(indexes.values());
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
   * @see Table#getPrivileges()
   */
  @Override
  public Collection<Privilege<Table>> getPrivileges()
  {
    return new ArrayList<>(privileges.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Table#getRelatedTables(schemacrawler.schema.TableRelationshipType)
   */
  @Override
  public Collection<Table> getRelatedTables(final TableRelationshipType tableRelationshipType)
  {
    final Set<Table> relatedTables = new HashSet<>();
    if (tableRelationshipType != null
        && tableRelationshipType != TableRelationshipType.none)
    {
      final List<MutableForeignKey> foreignKeysList = new ArrayList<>(foreignKeys
        .values());
      for (final ForeignKey foreignKey: foreignKeysList)
      {
        for (final ForeignKeyColumnReference columnReference: foreignKey)
        {
          final Table parentTable = columnReference.getPrimaryKeyColumn()
            .getParent();
          final Table childTable = columnReference.getForeignKeyColumn()
            .getParent();
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

    final List<Table> relatedTablesList = new ArrayList<>(relatedTables);
    Collections.sort(relatedTablesList, NamedObjectSort.alphabetical);
    return relatedTablesList;
  }

  /**
   * {@inheritDoc}
   *
   * @see Table#getTableConstraints()
   */
  @Override
  public Collection<TableConstraint> getTableConstraints()
  {
    return new ArrayList<>(constraints.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see Table#getTableType()
   */
  @Override
  public TableType getTableType()
  {
    return tableType;
  }

  /**
   * {@inheritDoc}
   *
   * @see Table#getTriggers()
   */
  @Override
  public Collection<Trigger> getTriggers()
  {
    return new ArrayList<>(triggers.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.TypedObject#getType()
   */
  @Override
  public final TableType getType()
  {
    return getTableType();
  }

  @Override
  public boolean hasDefinition()
  {
    return definition.length() > 0;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Table#lookupColumn(java.lang.String)
   */
  @Override
  public Optional<MutableColumn> lookupColumn(final String name)
  {
    Optional<MutableColumn> optionalColumn = columns.lookup(this, name);
    if (!optionalColumn.isPresent())
    {
      optionalColumn = hiddenColumns.lookup(this, name);
    }
    return optionalColumn;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Table#lookupForeignKey(java.lang.String)
   */
  @Override
  public Optional<MutableForeignKey> lookupForeignKey(final String name)
  {
    return foreignKeys.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Table#lookupIndex(java.lang.String)
   */
  @Override
  public Optional<MutableIndex> lookupIndex(final String name)
  {
    return indexes.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Table#lookupPrivilege(java.lang.String)
   */
  @Override
  public Optional<MutablePrivilege<Table>> lookupPrivilege(final String name)
  {
    return privileges.lookup(this, name);
  }

  /**
   * Looks up a trigger by name.
   *
   * @param triggerName
   *        Trigger name
   * @return Trigger, if found, or null
   */
  @Override
  public Optional<MutableTrigger> lookupTrigger(final String triggerName)
  {
    return triggers.lookup(this, triggerName);
  }

  void addColumn(final MutableColumn column)
  {
    columns.add(column);
  }

  void addForeignKey(final MutableForeignKey foreignKey)
  {
    foreignKeys.add(foreignKey);
  }

  void addHiddenColumn(final MutableColumn column)
  {
    hiddenColumns.add(column);
  }

  void addIndex(final MutableIndex index)
  {
    indexes.add(index);
  }

  void addPrivilege(final MutablePrivilege<Table> privilege)
  {
    privileges.add(privilege);
  }

  void addTableConstraint(final MutableTableConstraint tableConstraint)
  {
    constraints.add(tableConstraint);
  }

  void addTrigger(final MutableTrigger trigger)
  {
    triggers.add(trigger);
  }

  void appendDefinition(final String definition)
  {
    if (definition != null)
    {
      this.definition.append(definition);
    }
  }

  void setPrimaryKeyAndReplaceIndex(final MutablePrimaryKey primaryKey)
  {
    if (primaryKey == null)
    {
      return;
    }

    final String primaryKeyName = primaryKey.getName();
    final Optional<MutableIndex> indexOptional = indexes.lookup(this,
                                                                primaryKeyName);
    if (indexOptional.isPresent())
    {
      final MutableIndex index = indexOptional.get();
      indexes.remove(index);
      this.primaryKey = new MutablePrimaryKey(index);
    }
    else
    {
      this.primaryKey = primaryKey;
    }
  }

  void setSortIndex(final int sortIndex)
  {
    this.sortIndex = sortIndex;
  }

  void setTableType(final TableType tableType)
  {
    if (tableType == null)
    {
      this.tableType = TableType.UNKNOWN;
    }
    else
    {
      this.tableType = tableType;
    }
  }

  private Collection<ForeignKey> getForeignKeys(final TableAssociationType tableAssociationType)
  {
    final List<ForeignKey> foreignKeysList = new ArrayList<>(foreignKeys
      .values());
    if (tableAssociationType != null
        && tableAssociationType != TableAssociationType.all)
    {
      for (final Iterator<ForeignKey> iterator = foreignKeysList
        .iterator(); iterator.hasNext();)
      {
        final ForeignKey mutableForeignKey = iterator.next();
        boolean isExportedKey = false;
        boolean isImportedKey = false;
        for (final ForeignKeyColumnReference columnReference: mutableForeignKey)
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
