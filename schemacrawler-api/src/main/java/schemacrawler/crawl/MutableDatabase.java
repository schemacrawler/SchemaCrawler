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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

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

  private final MutableDatabaseInfo databaseInfo;
  private final MutableJdbcDriverInfo jdbcDriverInfo;
  private final MutableSchemaCrawlerInfo schemaCrawlerInfo;
  private final ColumnDataTypes systemColumnDataTypes = new ColumnDataTypes();
  private final Map<SchemaReference, MutableSchema> schemaRefsCache;

  MutableDatabase(final String name)
  {
    super(name, null);
    databaseInfo = new MutableDatabaseInfo();
    jdbcDriverInfo = new MutableJdbcDriverInfo();
    schemaCrawlerInfo = new MutableSchemaCrawlerInfo();
    schemaRefsCache = new HashMap<SchemaReference, MutableSchema>();
  }

  public MutableDatabaseInfo getDatabaseInfo()
  {
    return databaseInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getJdbcDriverInfo()
   */
  public MutableJdbcDriverInfo getJdbcDriverInfo()
  {
    return jdbcDriverInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSchema(java.lang.String)
   */
  public MutableSchema getSchema(final String name)
  {
    final Collection<SchemaReference> schemaRefs = getSchemaNames();
    for (final SchemaReference schemaRef: schemaRefs)
    {
      if (schemaRef.getFullName().equals(name))
      {
        return schemaRefsCache.get(schemaRef);
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSchemaCrawlerInfo()
   */
  public MutableSchemaCrawlerInfo getSchemaCrawlerInfo()
  {
    return schemaCrawlerInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSchemas()
   */
  public Schema[] getSchemas()
  {
    final List<MutableSchema> schemas = new ArrayList<MutableSchema>(schemaRefsCache
      .values());
    Collections.sort(schemas);
    return schemas.toArray(new Schema[schemas.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSystemColumnDataType(java.lang.String)
   */
  public MutableColumnDataType getSystemColumnDataType(final String name)
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

  MutableSchema addSchema(final SchemaReference schemaRef)
  {
    final MutableSchema schema = new MutableSchema(schemaRef);
    schemaRefsCache.put(schemaRef, schema);
    return schema;
  }

  MutableSchema addSchema(final String catalogName, final String schemaName)
  {
    return addSchema(new SchemaReference(catalogName, schemaName));
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
    final NamedObjectList<MutableProcedure> procedures = new NamedObjectList<MutableProcedure>();
    for (final Schema schema: getSchemas())
    {
      for (final Procedure procedure: schema.getProcedures())
      {
        procedures.add((MutableProcedure) procedure);
      }
    }
    return procedures;
  }

  NamedObjectList<MutableTable> getAllTables()
  {
    final NamedObjectList<MutableTable> tables = new NamedObjectList<MutableTable>();
    for (final Schema schema: getSchemas())
    {
      for (final Table table: schema.getTables())
      {
        tables.add((MutableTable) table);
      }
    }
    return tables;
  }

  MutableSchema getSchema(final SchemaReference schemaRef)
  {
    return schemaRefsCache.get(schemaRef);
  }

  Collection<SchemaReference> getSchemaNames()
  {
    return schemaRefsCache.keySet();
  }

  ColumnDataTypes getSystemColumnDataTypesList()
  {
    return systemColumnDataTypes;
  }

}
