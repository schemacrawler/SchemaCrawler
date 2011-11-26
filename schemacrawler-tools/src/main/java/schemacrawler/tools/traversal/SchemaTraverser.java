/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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
package schemacrawler.tools.traversal;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public class SchemaTraverser
{

  private Database database;
  private SchemaTraversalHandler handler;

  public Database getDatabase()
  {
    return database;
  }

  public SchemaTraversalHandler getFormatter()
  {
    return handler;
  }

  public void setDatabase(final Database database)
  {
    this.database = database;
  }

  public void setFormatter(final SchemaTraversalHandler formatter)
  {
    handler = formatter;
  }

  public final void traverse()
    throws SchemaCrawlerException
  {
    if (database == null || handler == null)
    {
      throw new SchemaCrawlerException("Cannot traverse database");
    }

    handler.begin();

    handler.handleInfoStart();
    handler.handle(database.getSchemaCrawlerInfo());
    handler.handle(database.getDatabaseInfo());
    handler.handle(database.getJdbcDriverInfo());
    handler.handleInfoEnd();

    final List<ColumnDataType> columnDataTypes = new ArrayList<ColumnDataType>();
    final List<Table> tables = new ArrayList<Table>();
    final List<Procedure> procedures = new ArrayList<Procedure>();

    columnDataTypes.addAll(Arrays.asList(database.getSystemColumnDataTypes()));
    for (final Schema schema: database.getSchemas())
    {
      columnDataTypes.addAll(Arrays.asList(schema.getColumnDataTypes()));
      tables.addAll(Arrays.asList(schema.getTables()));
      procedures.addAll(Arrays.asList(schema.getProcedures()));
    }

    if (!columnDataTypes.isEmpty())
    {
      handler.handleColumnDataTypesStart();
      for (final ColumnDataType columnDataType: columnDataTypes)
      {
        handler.handle(columnDataType);
      }
      handler.handleColumnDataTypesEnd();
    }

    if (!tables.isEmpty())
    {
      handler.handleTablesStart();
      for (final Table table: tables)
      {
        handler.handle(table);
      }
      handler.handleTablesEnd();
    }

    if (!procedures.isEmpty())
    {
      handler.handleProceduresStart();
      for (final Procedure procedure: procedures)
      {
        handler.handle(procedure);
      }
      handler.handleProceduresEnd();
    }

    handler.end();
  }

}
