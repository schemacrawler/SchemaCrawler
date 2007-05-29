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
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

/**
 * {@inheritDoc}
 * 
 * @author Sualeh Fatehi
 */
class MutableSchema
  extends AbstractDatabaseObject
  implements Schema
{

  private static final long serialVersionUID = 3258128063743931187L;

  private DatabaseInfo databaseInfo;
  private final NamedObjectList<MutableTable> tables = new NamedObjectList<MutableTable>(NamedObjectSort.natural);
  private final NamedObjectList<MutableProcedure> procedures = new NamedObjectList<MutableProcedure>(NamedObjectSort.natural);

  MutableSchema(final String catalogName,
                final String schemaName,
                final String name)
  {
    super(catalogName, schemaName, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#equals(java.lang.Object)
   */
  @Override
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
   * @see schemacrawler.schema.Schema#getDatabaseInfo()
   */
  public DatabaseInfo getDatabaseInfo()
  {
    return databaseInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getProcedures()
   */
  public Procedure[] getProcedures()
  {
    return procedures.getAll().toArray(new Procedure[0]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getTables()
   */
  public Table[] getTables()
  {
    return tables.getAll().toArray(new Table[0]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#hashCode()
   */
  @Override
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

  /**
   * Adds a procedure.
   * 
   * @param procedure
   *        Procedure
   */
  void addProcedure(final MutableProcedure procedure)
  {
    procedures.add(procedure);
  }

  /**
   * Adds a table.
   * 
   * @param table
   *        Table
   */
  void addTable(final MutableTable table)
  {
    tables.add(table);
  }

  void setDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    this.databaseInfo = databaseInfo;
  }
}
