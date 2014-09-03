/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.DatabaseWithAssociations;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.tools.traversal.SchemaTraverser;

/**
 * Basic SchemaCrawler executor.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaTextExecutable
  extends BaseStagedExecutable
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaTextExecutable.class.getName());

  private SchemaTextOptions schemaTextOptions;

  public SchemaTextExecutable(final String command)
  {
    super(command);
  }

  @Override
  public void executeOn(final Database db, final Connection connection)
    throws Exception
  {
    loadSchemaTextOptions();
    checkOutputFormat();

    InfoLevel infoLevel;
    try
    {
      infoLevel = InfoLevel.valueOf(schemaCrawlerOptions.getSchemaInfoLevel()
        .getTag());
    }
    catch (final Exception e)
    {
      infoLevel = InfoLevel.unknown;
    }

    final Database database;
    if (infoLevel == InfoLevel.maximum)
    {
      database = new DatabaseWithAssociations(db);
    }
    else
    {
      database = db;
    }

    final SchemaTraversalHandler formatter = getSchemaTraversalHandler();

    final SchemaTraverser traverser = new SchemaTraverser();
    traverser.setDatabase(database);
    traverser.setHandler(formatter);
    traverser.traverse();

  }

  public final SchemaTextOptions getSchemaTextOptions()
  {
    loadSchemaTextOptions();
    return schemaTextOptions;
  }

  public final void setSchemaTextOptions(final SchemaTextOptions schemaTextOptions)
  {
    this.schemaTextOptions = schemaTextOptions;
  }

  private void checkOutputFormat()
  {
    if (!outputOptions.hasOutputFormat())
    {
      LOGGER.log(Level.CONFIG,
                 "Unknown output format: "
                     + outputOptions.getOutputFormatValue());
    }
  }

  private SchemaTextDetailType getSchemaTextDetailType()
  {
    SchemaTextDetailType schemaTextDetailType;
    try
    {
      schemaTextDetailType = SchemaTextDetailType.valueOf(command);
    }
    catch (final IllegalArgumentException e)
    {
      schemaTextDetailType = SchemaTextDetailType.schema;
    }
    return schemaTextDetailType;
  }

  private SchemaTraversalHandler getSchemaTraversalHandler()
    throws SchemaCrawlerException
  {
    final SchemaTextDetailType schemaTextDetailType = getSchemaTextDetailType();
    final SchemaTraversalHandler formatter;

    final OutputFormat outputFormat = outputOptions.getOutputFormat();
    if (outputFormat == TextOutputFormat.json)
    {
      formatter = new SchemaJsonFormatter(schemaTextDetailType,
                                          schemaTextOptions,
                                          outputOptions);
    }
    else if (schemaTextDetailType == SchemaTextDetailType.list)
    {
      formatter = new SchemaListFormatter(schemaTextDetailType,
                                          schemaTextOptions,
                                          outputOptions);
    }

    else
    {
      formatter = new SchemaTextFormatter(schemaTextDetailType,
                                          schemaTextOptions,
                                          outputOptions);
    }

    return formatter;
  }

  private void loadSchemaTextOptions()
  {
    if (schemaTextOptions == null)
    {
      schemaTextOptions = new SchemaTextOptions(additionalConfiguration);
    }
  }

}
