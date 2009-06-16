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


import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.WeakAssociations;

/**
 * Database and connection information. Created from metadata returned
 * by a JDBC call, and other sources of information.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableDatabase
  extends AbstractNamedObject
  implements Database
{

  private static final long serialVersionUID = 4051323422934251828L;

  private final DatabaseInfo databaseInfo;
  private final ColumnDataTypes systemColumnDataTypes = new ColumnDataTypes();
  private final NamedObjectList<MutableCatalog> catalogs = new NamedObjectList<MutableCatalog>(NamedObjectSort.alphabetical);
  private WeakAssociations weakAssociations;

  MutableDatabase(final String name)
  {
    super(name);
    databaseInfo = new MutableDatabaseInfo();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Catalog#getCatalog(java.lang.String)
   */
  public Catalog getCatalog(final String name)
  {
    return catalogs.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Catalog#getCatalogs()
   */
  public Catalog[] getCatalogs()
  {
    return catalogs.values().toArray(new Catalog[catalogs.size()]);
  }

  public DatabaseInfo getDatabaseInfo()
  {
    return databaseInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSystemColumnDataType(java.lang.String)
   */
  public ColumnDataType getSystemColumnDataType(final String name)
  {
    return systemColumnDataTypes.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSystemColumnDataTypes()
   */
  public ColumnDataType[] getSystemColumnDataTypes()
  {
    return systemColumnDataTypes.values()
      .toArray(new ColumnDataType[systemColumnDataTypes.size()]);
  }

  public WeakAssociations getWeakAssociations()
  {
    return weakAssociations;
  }

  void addCatalog(final MutableCatalog catalog)
  {
    catalogs.add(catalog);
  }

  void addSystemColumnDataType(final MutableColumnDataType columnDataType)
  {
    if (columnDataType != null)
    {
      systemColumnDataTypes.add(columnDataType);
    }
  }

  NamedObjectList<MutableProcedure> getAllProcedures()
  {
    final NamedObjectList<MutableProcedure> procedures = new NamedObjectList<MutableProcedure>(NamedObjectSort.alphabetical);
    for (final Catalog catalog: getCatalogs())
    {
      for (final Schema schema: catalog.getSchemas())
      {
        for (final Procedure procedure: schema.getProcedures())
        {
          procedures.add((MutableProcedure) procedure);
        }
      }
    }

    return procedures;
  }

  NamedObjectList<MutableTable> getAllTables()
  {
    final NamedObjectList<MutableTable> tables = new NamedObjectList<MutableTable>(NamedObjectSort.alphabetical);
    for (final Catalog catalog: getCatalogs())
    {
      for (final Schema schema: catalog.getSchemas())
      {
        for (final Table table: schema.getTables())
        {
          tables.add((MutableTable) table);
        }
      }
    }

    return tables;
  }

  ColumnDataTypes getSystemColumnDataTypesList()
  {
    return systemColumnDataTypes;
  }

  MutableProcedure lookupProcedure(final String catalogName,
                                   final String schemaName,
                                   final String procedureName)
  {
    MutableProcedure procedure = null;
    final MutableSchema schema = lookupSchema(catalogName, schemaName);
    if (schema != null)
    {
      procedure = (MutableProcedure) schema.getProcedure(procedureName);
    }
    return procedure;
  }

  MutableSchema lookupSchema(final String catalogName, final String schemaName)
  {
    MutableSchema schema = null;
    final Catalog catalog = getCatalog(catalogName);
    if (catalog != null)
    {
      schema = (MutableSchema) catalog.getSchema(schemaName);
    }
    return schema;
  }

  MutableTable lookupTable(final String catalogName,
                           final String schemaName,
                           final String tableName)
  {
    MutableTable table = null;
    final MutableSchema schema = lookupSchema(catalogName, schemaName);
    if (schema != null)
    {
      table = (MutableTable) schema.getTable(tableName);
    }
    return table;
  }

  void setWeakAssociations(final WeakAssociations weakAssociations)
  {
    this.weakAssociations = weakAssociations;
  }

}
