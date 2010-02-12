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

package schemacrawler.tools.text.schema;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.DatabaseLint;
import schemacrawler.tools.analysis.DatabaseWithWeakAssociations;
import schemacrawler.tools.commandline.InfoLevel;
import schemacrawler.tools.executable.BaseExecutable;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaTextExecutable
  extends BaseExecutable
{

  private static final long serialVersionUID = -6824567755397315920L;

  private SchemaTextOptions schemaTextOptions;

  public SchemaTextExecutable(final String command)
  {
    super(command);
  }

  public final SchemaTextOptions getSchemaTextOptions()
  {
    final SchemaTextOptions schemaTextOptions;
    if (this.schemaTextOptions == null)
    {
      schemaTextOptions = new SchemaTextOptions(additionalConfiguration);
    }
    else
    {
      schemaTextOptions = this.schemaTextOptions;
    }
    return schemaTextOptions;
  }

  public final void setSchemaTextOptions(final SchemaTextOptions schemaTextOptions)
  {
    this.schemaTextOptions = schemaTextOptions;
  }

  @Override
  protected void executeOn(final Database db, final Connection connection)
    throws Exception
  {
    // Determine what decorators to apply to the database
    InfoLevel infoLevel;
    try
    {
      infoLevel = InfoLevel.valueOf(getSchemaCrawlerOptions()
        .getSchemaInfoLevel().getTag());
    }
    catch (final Exception e)
    {
      infoLevel = InfoLevel.unknown;
    }

    Database database = db;
    if (infoLevel.ordinal() >= InfoLevel.maximum.ordinal())
    {
      database = new DatabaseWithWeakAssociations(database);
    }
    if (infoLevel.ordinal() >= InfoLevel.lint.ordinal())
    {
      database = new DatabaseLint(database);
    }

    final SchemaTextFormatter formatter = getDatabaseTraversalHandler();

    formatter.begin();
    formatter.handle(database.getSchemaCrawlerInfo(), database
      .getDatabaseInfo(), database.getJdbcDriverInfo());

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
      formatter.handleColumnDataTypesStart();
      for (final ColumnDataType columnDataType: columnDataTypes)
      {
        formatter.handle(columnDataType);
      }
      formatter.handleColumnDataTypesEnd();
    }

    if (!tables.isEmpty())
    {
      formatter.handleTablesStart();
      for (final Table table: tables)
      {
        formatter.handle(table);
      }
      formatter.handleTablesEnd();
    }

    if (!procedures.isEmpty())
    {
      formatter.handleProceduresStart();
      for (final Procedure procedure: procedures)
      {
        formatter.handle(procedure);
      }
      formatter.handleProceduresEnd();
    }

    formatter.end();

  }

  private SchemaTextFormatter getDatabaseTraversalHandler()
    throws SchemaCrawlerException
  {
    final SchemaTextFormatter formatter;
    SchemaTextDetailType schemaTextDetailType;
    try
    {
      schemaTextDetailType = SchemaTextDetailType.valueOf(command);
    }
    catch (final IllegalArgumentException e)
    {
      schemaTextDetailType = SchemaTextDetailType.standard_schema;
    }
    final SchemaTextOptions schemaTextOptions = getSchemaTextOptions();
    formatter = new SchemaTextFormatter(schemaTextDetailType,
                                        schemaTextOptions,
                                        outputOptions);

    return formatter;
  }

}
