/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.TableType;
import schemacrawler.schema.Trigger;

final class TablePartial
  extends AbstractDatabaseObject
  implements Table, PartialDatabaseObject
{

  private static final long serialVersionUID = -5968964551235088703L;

  private Column column;
  private ForeignKey foreignKey;

  TablePartial(final Schema schema, final String tableName)
  {
    super(schema, tableName);
  }

  TablePartial(final Table table)
  {
    this(requireNonNull(table, "No table provided").getSchema(),
         table.getName());
    addAttributes(table.getAttributes());
  }

  @Override
  public List<Column> getColumns()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public String getDefinition()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<ForeignKey> getExportedForeignKeys()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<ForeignKey> getForeignKeys()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<ForeignKey> getImportedForeignKeys()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<Index> getIndexes()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public PrimaryKey getPrimaryKey()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<Privilege<Table>> getPrivileges()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<Table> getRelatedTables(final TableRelationshipType tableRelationshipType)
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<TableConstraint> getTableConstraints()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public TableType getTableType()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<Trigger> getTriggers()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public TableType getType()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean hasDefinition()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Optional<Column> lookupColumn(final String name)
  {
    if (column.getName().equals(name))
    {
      return Optional.ofNullable(column);
    }
    else
    {
      return Optional.empty();
    }
  }

  @Override
  public Optional<ForeignKey> lookupForeignKey(final String name)
  {
    if (foreignKey.getName().equals(name))
    {
      return Optional.ofNullable(foreignKey);
    }
    else
    {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Index> lookupIndex(final String name)
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Optional<? extends Privilege<Table>> lookupPrivilege(final String name)
  {
    throw new NotLoadedException(this);
  }

  @Override
  public Optional<Trigger> lookupTrigger(final String name)
  {
    throw new NotLoadedException(this);
  }

  void addColumn(final Column column)
  {
    this.column = column;
  }

  void addForeignKey(final ForeignKey foreignKey)
  {
    this.foreignKey = foreignKey;
  }

}
