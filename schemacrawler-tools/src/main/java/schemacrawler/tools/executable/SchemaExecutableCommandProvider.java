/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.tools.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextExecutable;

import java.util.ArrayList;
import java.util.Collection;

import static sf.util.Utility.isBlank;

public final class SchemaExecutableCommandProvider
  extends ExecutableCommandProvider
{

  private static Collection<String> supportedCommands = supportedCommands();

  private static Collection<String> supportedCommands()
  {
    final Collection<String> supportedCommands = new ArrayList<>();
    for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
      .values())
    {
      supportedCommands.add(schemaTextDetailType.name());
    }
    return supportedCommands;
  }

  public SchemaExecutableCommandProvider()
  {
    super(supportedCommands, "");
  }

  @Override
  public StagedExecutable configureNewExecutable(final String command,
                                                 final SchemaCrawlerOptions schemaCrawlerOptions,
                                                 final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    final boolean supportsCommand = supportsCommand(command,
                                                    schemaCrawlerOptions,
                                                    outputOptions);
    if (!supportsCommand)
    {
      throw new SchemaCrawlerException(String
        .format("Command <%s> not supported", command));
    }

    // Create and configure executable
    final StagedExecutable executable = new SchemaTextExecutable(command);
    if (schemaCrawlerOptions != null)
    {
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    }
    if (outputOptions != null)
    {
      executable.setOutputOptions(outputOptions);
    }

    return executable;

  }

  @Override
  public Collection<String> getSupportedCommands()
  {
    return supportedCommands();
  }

  @Override
  public boolean supportsCommand(final String command,
                                 final SchemaCrawlerOptions schemaCrawlerOptions,
                                 final OutputOptions outputOptions)
  {
    if (isBlank(command) || outputOptions == null)
    {
      return false;
    }
    final String outputFormatValue = outputOptions.getOutputFormatValue();
    return supportedCommands.contains(command)
           && (isBlank(outputFormatValue)
               || TextOutputFormat.isSupportedFormat(outputFormatValue));
  }

}
