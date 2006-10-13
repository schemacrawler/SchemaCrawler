/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.util.List;

import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.util.NaturalSortComparator;

/**
 * {@inheritDoc}
 * 
 * @author sfatehi
 */
class MutableSchema
  extends AbstractDatabaseObject
  implements Schema
{

  private static final long serialVersionUID = 3258128063743931187L;

  private DatabaseInfo databaseInfo;
  private final NamedObjectList tables = new NamedObjectList(
                                                             new NaturalSortComparator());
  private final NamedObjectList procedures = new NamedObjectList(
                                                                 new NaturalSortComparator());

  MutableSchema(String schemaName, String catalogName, String name)
  {
    super(schemaName, catalogName, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getDatabaseInfo()
   */
  public DatabaseInfo getDatabaseInfo()
  {
    return databaseInfo;
  }

  void setDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    this.databaseInfo = databaseInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getProcedures()
   */
  public Procedure[] getProcedures()
  {
    final List allProcedures = procedures.getAll();
    return (Procedure[]) allProcedures.toArray(new Procedure[allProcedures
      .size()]);
  }

  /**
   * Adds a procedure.
   * 
   * @param procedure
   *        Procedure
   */
  void addProcedure(final Procedure procedure)
  {
    procedures.add(procedure);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getTables()
   */
  public Table[] getTables()
  {
    final List allTables = tables.getAll();
    return (Table[]) allTables.toArray(new Table[allTables.size()]);
  }

  /**
   * Adds a table.
   * 
   * @param table
   *        Table
   */
  void addTable(final Table table)
  {
    tables.add(table);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#equals(java.lang.Object)
   */
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof MutableSchema))
    {
      return false;
    }
    if (!super.equals(o))
    {
      return false;
    }

    final MutableSchema mutableSchema = (MutableSchema) o;

    if (!databaseInfo.equals(mutableSchema.databaseInfo))
    {
      return false;
    }
    if (!tables.equals(mutableSchema.tables))
    {
      return false;
    }

    if (!procedures.equals(mutableSchema.procedures))
    {
      return false;
    }

    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#hashCode()
   */
  public int hashCode()
  {
    int result = super.hashCode();
    if (databaseInfo != null)
    {
      result = 29 * result + databaseInfo.hashCode();
    }
    if (tables != null)
    {
      result = 29 * result + tables.hashCode();
    }
    if (procedures != null)
    {
      result = 29 * result + procedures.hashCode();
    }
    return result;
  }
}
