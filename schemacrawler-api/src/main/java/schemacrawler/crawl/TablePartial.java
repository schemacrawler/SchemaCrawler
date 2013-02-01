/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.TableType;
import schemacrawler.schema.Trigger;

final class TablePartial
  extends AbstractDatabaseObject
  implements Table
{

  private static final long serialVersionUID = -5968964551235088703L;

  private Column column;
  private ForeignKey foreignKey;

  TablePartial(final Schema schema, final String tableName)
  {
    super(schema, tableName);
  }

  @Override
  public Collection<CheckConstraint> getCheckConstraints()
  {
    throw new NotLoadedException();
  }

  @Override
  public Column getColumn(final String name)
  {
    if (column.getName().equals(name))
    {
      return column;
    }
    else
    {
      return null;
    }
  }

  @Override
  public List<Column> getColumns()
  {
    if (column != null)
    {
      return new ArrayList<Column>(Arrays.asList(column));
    }
    else
    {
      return Collections.emptyList();
    }
  }

  @Override
  public String getColumnsListAsString()
  {
    throw new NotLoadedException();
  }

  @Override
  public Collection<ForeignKey> getExportedForeignKeys()
  {
    throw new NotLoadedException();
  }

  @Override
  public ForeignKey getForeignKey(final String name)
  {
    if (foreignKey.getName().equals(name))
    {
      return foreignKey;
    }
    else
    {
      return null;
    }
  }

  @Override
  public Collection<ForeignKey> getForeignKeys()
  {
    if (foreignKey != null)
    {
      return new ArrayList<ForeignKey>(Arrays.asList(foreignKey));
    }
    else
    {
      return Collections.emptyList();
    }
  }

  @Override
  public Collection<ForeignKey> getImportedForeignKeys()
  {
    throw new NotLoadedException();
  }

  @Override
  public Index getIndex(final String name)
  {
    throw new NotLoadedException();
  }

  @Override
  public Collection<Index> getIndices()
  {
    throw new NotLoadedException();
  }

  @Override
  public PrimaryKey getPrimaryKey()
  {
    throw new NotLoadedException();
  }

  @Override
  public Privilege<Table> getPrivilege(final String name)
  {
    throw new NotLoadedException();
  }

  @Override
  public Collection<Privilege<Table>> getPrivileges()
  {
    throw new NotLoadedException();
  }

  @Override
  public Collection<Table> getRelatedTables(final TableRelationshipType tableRelationshipType)
  {
    throw new NotLoadedException();
  }

  @Override
  public TableType getTableType()
  {
    throw new NotLoadedException();
  }

  @Override
  public Trigger getTrigger(final String name)
  {
    throw new NotLoadedException();
  }

  @Override
  public Collection<Trigger> getTriggers()
  {
    throw new NotLoadedException();
  }

  @Override
  public TableType getType()
  {
    throw new NotLoadedException();
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
