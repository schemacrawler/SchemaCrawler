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

package schemacrawler.tools;


import schemacrawler.crawl.InformationSchemaViews;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.tools.datatext.DataTextFormatOptions;
import schemacrawler.tools.operation.Operation;
import schemacrawler.tools.operation.OperatorOptions;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextOptions;
import sf.util.Config;

/**
 * Program options.
 * 
 * @author Sualeh Fatehi
 */
public final class ExecutionContext
{

  private final ToolType toolType;
  private final SchemaCrawlerOptions schemaCrawlerOptions;
  private final InformationSchemaViews informationSchemaViews;
  private final BaseToolOptions toolOptions;

  /**
   * Parses options from a properties file.
   * 
   * @param config
   *        Properties
   * @param command
   *        Properties
   * @param pageOptions
   *        Page options
   */
  public ExecutionContext(final Command command,
                          final Config config,
                          final OutputOptions outputOptions)
  {
    if (config == null || command == null || outputOptions == null)
    {
      throw new IllegalArgumentException("All arguments need to be specified");
    }

    toolType = determineToolType(command, config);
    schemaCrawlerOptions = new SchemaCrawlerOptions(config);
    informationSchemaViews = new InformationSchemaViews(config);

    BaseToolOptions toolOptions = null;
    switch (toolType)
    {
      case schema_text:
        final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType
          .valueOf(command.getName());
        toolOptions = new SchemaTextOptions(config,
                                            outputOptions,
                                            schemaTextDetailType);
        break;
      case operation:
        Operation operation;
        try
        {
          operation = Operation.valueOf(command.getName());
          toolOptions = new OperatorOptions(config, outputOptions, operation);
        }
        catch (final IllegalArgumentException e)
        {
          final String queryName = command.getName();
          toolOptions = new OperatorOptions(config, outputOptions, queryName);
        }
        break;
      case data_text:
        final String queryName = command.getName();
        toolOptions = new DataTextFormatOptions(config,
                                                outputOptions,
                                                queryName);
        break;
    }
    this.toolOptions = toolOptions;

  }

  public ToolType determineToolType(final Command command, final Config config)
  {
    ToolType toolType;
    if (!command.isQuery())
    {
      toolType = ToolType.schema_text;
    }
    else
    {
      Operation operation;
      try
      {
        operation = Operation.valueOf(command.getName());
      }
      catch (final IllegalArgumentException e)
      {
        operation = null;
      }
      if (operation == null)
      {
        Query query = new Query(command.getName(), config
          .get(command.getName()));
        if (query.isQueryOver())
        {
          toolType = ToolType.data_text;
        }
        else
        {
          toolType = ToolType.operation;
        }
      }
      else
      {
        toolType = ToolType.operation;
      }
    }
    return toolType;
  }

  public InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  /**
   * Gets the data text format options.
   * 
   * @return Data text format options
   */
  public BaseToolOptions getToolOptions()
  {
    return toolOptions;
  }

  /**
   * Gets the schema crawler options.
   * 
   * @return SchemaCrawlerOptions
   */
  public SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  /**
   * Gets the tool type.
   * 
   * @return Tool type
   */
  public ToolType getToolType()
  {
    return toolType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("ExecutionContext[");
    buffer.append("; ").append(toolType);
    buffer.append("; ").append(schemaCrawlerOptions);
    buffer.append("; ").append(informationSchemaViews);
    buffer.append("; ").append(toolOptions);
    buffer.append("]");
    return buffer.toString();
  }

}
