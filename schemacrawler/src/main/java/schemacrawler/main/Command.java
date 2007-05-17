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

package schemacrawler.main;


import schemacrawler.tools.ToolType;
import schemacrawler.tools.operation.Operation;
import schemacrawler.tools.schematext.SchemaTextDetailType;

/**
 * A single command from the command line.
 * 
 * @author sfatehi
 */
final class Command
{

  /**
   * Creates a command.
   * 
   * @param textOutputFormatType
   *        Type of text output to generate
   * @param schemaTextDetailType
   *        Text output detail
   * @param operation
   *        Operation
   * @param query
   *        Query
   * @return Command
   */
  static Command createCommand(final SchemaTextDetailType schemaTextDetailType,
                               final Operation operation,
                               final String query)
  {

    ToolType toolType = null;
    // Calculate the tool type from the command
    if (schemaTextDetailType == null && operation == null)
    {
      // Single query
      toolType = ToolType.data_text;
    }
    else if (schemaTextDetailType != null)
    {
      // Crawl schema
      toolType = ToolType.schema_text;
    }
    else if (operation != null)
    {
      // Operation
      toolType = ToolType.operation;
    }

    return new Command(toolType, schemaTextDetailType, operation, query);
  }

  private final ToolType toolType;
  private final SchemaTextDetailType schemaTextDetailType;
  private final Operation operation;

  private final String query;

  /**
   * Constructor.
   * 
   * @param textOutputFormatType
   * @param toolType
   * @param outputFile
   * @param schemaTextDetailType
   * @param operation
   * @param query
   */
  private Command(final ToolType toolType,
                  final SchemaTextDetailType schemaTextDetailType,
                  final Operation operation,
                  final String query)
  {
    this.toolType = toolType;
    this.schemaTextDetailType = schemaTextDetailType;
    this.operation = operation;
    this.query = query;
  }

  /**
   * Operation linked to this command.
   * 
   * @return Operation
   */
  Operation getOperation()
  {
    return operation;
  }

  /**
   * Query for this command.
   * 
   * @return Query
   */
  String getQuery()
  {
    return query;
  }

  /**
   * Text output detail.
   * 
   * @return Text output detail.
   */
  SchemaTextDetailType getSchemaTextDetailType()
  {
    return schemaTextDetailType;
  }

  /**
   * Tool type.
   * 
   * @return Tool type.
   */
  ToolType getToolType()
  {
    return toolType;
  }

}
