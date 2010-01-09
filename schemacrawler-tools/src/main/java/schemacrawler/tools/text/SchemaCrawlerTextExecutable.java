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

package schemacrawler.tools.text;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Database;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.operation.OperationExecutable;
import schemacrawler.tools.text.operation.OperationOptions;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import sf.util.Utility;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerTextExecutable
  extends BaseExecutable
{

  private static final long serialVersionUID = -6824567755397315920L;

  private OperationOptions operationOptions;
  private SchemaTextOptions schemaTextOptions;

  public SchemaCrawlerTextExecutable(final String command)
  {
    super(command);
  }

  public final OperationOptions getOperationOptions()
  {
    final OperationOptions operationOptions;
    if (this.operationOptions == null)
    {
      operationOptions = new OperationOptions(additionalConfiguration);
    }
    else
    {
      operationOptions = this.operationOptions;
    }
    return operationOptions;
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

  public final void setOperationOptions(final OperationOptions operationOptions)
  {
    this.operationOptions = operationOptions;
  }

  public final void setSchemaTextOptions(final SchemaTextOptions schemaTextOptions)
  {
    this.schemaTextOptions = schemaTextOptions;
  }

  @Override
  protected final void executeOn(final Database database,
                                 final Connection connection)
    throws Exception
  {
    final Commands commands;
    if (Utility.isBlank(command))
    {
      commands = new Commands(SchemaTextDetailType.standard_schema.name());
    }
    else
    {
      commands = new Commands(command);
    }

    final List<Executable> handlers = new ArrayList<Executable>();
    for (final String command: commands)
    {
      final OutputOptions outputOptions = this.outputOptions.duplicate();
      if (commands.size() > 1)
      {
        if (commands.isFirstCommand(command))
        {
          // First command - no footer
          outputOptions.setNoFooter(true);
        }
        else if (commands.isLastCommand(command))
        {
          // Last command - no header, or info
          outputOptions.setNoHeader(true);
          outputOptions.setNoInfo(true);

          outputOptions.setAppendOutput(true);
        }
        else
        {
          // Middle command - no header, footer, or info
          outputOptions.setNoHeader(true);
          outputOptions.setNoInfo(true);
          outputOptions.setNoFooter(true);

          outputOptions.setAppendOutput(true);
        }
      }

      SchemaTextDetailType schemaTextDetailType;
      try
      {
        schemaTextDetailType = SchemaTextDetailType.valueOf(command);
      }
      catch (final Exception e)
      {
        schemaTextDetailType = null;
      }
      if (schemaTextDetailType != null)
      {
        final SchemaTextExecutable executable = new SchemaTextExecutable(command);
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setAdditionalConfiguration(additionalConfiguration);
        executable.setOutputOptions(outputOptions);
        executable.setSchemaTextOptions(schemaTextOptions);
        // 
        handlers.add(executable);
      }
      else
      {
        final OperationExecutable executable = new OperationExecutable(command);
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setAdditionalConfiguration(additionalConfiguration);
        executable.setOutputOptions(outputOptions);
        executable.setOperationOptions(operationOptions);
        // 
        handlers.add(executable);
      }
    }

    for (final Executable executable: handlers)
    {
      executable.execute(connection);
    }

  }

}
