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
package schemacrawler.schemacrawler;


import java.util.Collection;
import java.util.Map;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;

public abstract class BaseDatabaseDecorator
  implements Database
{

  private static final long serialVersionUID = -3953296149824921463L;

  protected final Database database;

  public BaseDatabaseDecorator(final Database database)
  {
    if (database == null)
    {
      throw new IllegalArgumentException("No database provided");
    }
    this.database = database;
  }

  @Override
  public int compareTo(final NamedObject o)
  {
    return database.compareTo(o);
  }

  @Override
  public Object getAttribute(final String name)
  {
    return database.getAttribute(name);
  }

  @Override
  public <T> T getAttribute(final String name, final T defaultValue)
  {
    return database.getAttribute(name, defaultValue);
  }

  @Override
  public Map<String, Object> getAttributes()
  {
    return database.getAttributes();
  }

  @Override
  public ColumnDataType getColumnDataType(final Schema schema, final String name)
  {
    return database.getColumnDataType(schema, name);
  }

  @Override
  public Collection<ColumnDataType> getColumnDataTypes()
  {
    return database.getColumnDataTypes();
  }

  @Override
  public Collection<ColumnDataType> getColumnDataTypes(final Schema schema)
  {
    return database.getColumnDataTypes(schema);
  }

  @Override
  public DatabaseInfo getDatabaseInfo()
  {
    return database.getDatabaseInfo();
  }

  @Override
  public String getFullName()
  {
    return database.getFullName();
  }

  @Override
  public JdbcDriverInfo getJdbcDriverInfo()
  {
    return database.getJdbcDriverInfo();
  }

  @Override
  public String getLookupKey()
  {
    return getFullName();
  }

  @Override
  public String getName()
  {
    return database.getName();
  }

  @Override
  public String getRemarks()
  {
    return database.getRemarks();
  }

  @Override
  public Routine getRoutine(final Schema schema, final String name)
  {
    return database.getRoutine(schema, name);
  }

  @Override
  public Collection<Routine> getRoutines()
  {
    return database.getRoutines();
  }

  @Override
  public Collection<Routine> getRoutines(final Schema schema)
  {
    return database.getRoutines(schema);
  }

  @Override
  public Schema getSchema(final String name)
  {
    return database.getSchema(name);
  }

  @Override
  public SchemaCrawlerInfo getSchemaCrawlerInfo()
  {
    return database.getSchemaCrawlerInfo();
  }

  @Override
  public Collection<Schema> getSchemas()
  {
    return database.getSchemas();
  }

  @Override
  public Synonym getSynonym(final Schema schema, final String name)
  {
    return database.getSynonym(schema, name);
  }

  @Override
  public Collection<Synonym> getSynonyms()
  {
    return database.getSynonyms();
  }

  @Override
  public Collection<Synonym> getSynonyms(final Schema schema)
  {
    return database.getSynonyms(schema);
  }

  @Override
  public ColumnDataType getSystemColumnDataType(final String name)
  {
    return database.getSystemColumnDataType(name);
  }

  @Override
  public Collection<ColumnDataType> getSystemColumnDataTypes()
  {
    return database.getSystemColumnDataTypes();
  }

  @Override
  public Table getTable(final Schema schema, final String name)
  {
    return database.getTable(schema, name);
  }

  @Override
  public Collection<Table> getTables()
  {
    return database.getTables();
  }

  @Override
  public Collection<Table> getTables(final Schema schema)
  {
    return database.getTables(schema);
  }

  @Override
  public void setAttribute(final String name, final Object value)
  {
    database.setAttribute(name, value);
  }

}
