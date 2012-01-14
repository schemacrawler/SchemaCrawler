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
package schemacrawler.tools.analysis.associations;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Table;

public final class DatabaseWithAssociations
  implements Database
{

  private static final long serialVersionUID = -3953296149824921463L;

  private final Database database;
  private final WeakAssociationsCollector collector;

  public DatabaseWithAssociations(final Database database)
  {
    if (database == null)
    {
      throw new IllegalArgumentException("No database provided");
    }
    this.database = database;

    final List<Table> allTables = new ArrayList<Table>();
    for (final Schema schema: database.getSchemas())
    {
      for (final Table table: schema.getTables())
      {
        allTables.add(table);
      }
    }
    collector = new SimpleWeakAssociationsCollector();
    final WeakAssociationsAnalyzer weakAssociationsAnalyzer = new WeakAssociationsAnalyzer(allTables,
                                                                                           collector);
    weakAssociationsAnalyzer.analyzeTables();

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

  public WeakAssociationsCollector getCollector()
  {
    return collector;
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
  public Schema[] getSchemas()
  {
    return database.getSchemas();
  }

  @Override
  public ColumnDataType getSystemColumnDataType(final String name)
  {
    return database.getSystemColumnDataType(name);
  }

  @Override
  public ColumnDataType[] getSystemColumnDataTypes()
  {
    return database.getSystemColumnDataTypes();
  }

  @Override
  public void setAttribute(final String name, final Object value)
  {
    database.setAttribute(name, value);
  }

}
