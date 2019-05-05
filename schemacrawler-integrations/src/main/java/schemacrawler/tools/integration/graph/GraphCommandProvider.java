/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.integration.graph;


import static sf.util.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.ExecutableCommandProvider;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public final class GraphCommandProvider
  extends ExecutableCommandProvider
{

  private static Collection<CommandDescription> supportedCommands()
  {
    final Collection<CommandDescription> supportedCommands = new ArrayList<>();
    for (final SchemaTextDetailType schemaTextDetailType : SchemaTextDetailType.values())
    {
      supportedCommands.add(new CommandDescription(schemaTextDetailType.name(),
                                                   schemaTextDetailType.getDescription()));
    }
    return supportedCommands;
  }

  public GraphCommandProvider()
  {
    super(supportedCommands());
  }

  @Override
  public SchemaCrawlerCommand newSchemaCrawlerCommand(final String command)
  {
    return new GraphRenderer(command);
  }

  @Override
  public boolean supportsSchemaCrawlerCommand(final String command,
                                              final SchemaCrawlerOptions schemaCrawlerOptions,
                                              final OutputOptions outputOptions)
  {
    if (outputOptions == null)
    {
      return false;
    }
    final String format = outputOptions.getOutputFormatValue();
    if (isBlank(format))
    {
      return false;
    }
    final GraphOutputFormat graphOutputFormat = GraphOutputFormat.fromFormat(
      format);
    final boolean supportsSchemaCrawlerCommand =
      supportsCommand(command) && GraphOutputFormat.isSupportedFormat(format)
      && graphOutputFormat != GraphOutputFormat.htmlx;
    return supportsSchemaCrawlerCommand;
  }

}
