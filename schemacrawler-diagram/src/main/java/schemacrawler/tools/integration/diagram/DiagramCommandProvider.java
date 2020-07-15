/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.integration.diagram;


import static sf.util.Utility.isBlank;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.CommandProviderUtility;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.options.OutputOptions;

public final class DiagramCommandProvider
  extends BaseCommandProvider
{

  public DiagramCommandProvider()
  {
    super(CommandProviderUtility.schemaTextCommands());
  }

  @Override
  public SchemaCrawlerCommand newSchemaCrawlerCommand(final String command)
  {
    return new DiagramRenderer(command);
  }

  @Override
  public boolean supportsSchemaCrawlerCommand(final String command,
                                              final SchemaCrawlerOptions schemaCrawlerOptions,
                                              final Config additionalConfiguration,
                                              final OutputOptions outputOptions)
  {
    return supportsCommand(command);
  }

  @Override
  public boolean supportsOutputFormat(final String command,
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
    final DiagramOutputFormat diagramOutputFormat =
      DiagramOutputFormat.fromFormat(format);
    final boolean supportsOutputFormat =
      DiagramOutputFormat.isSupportedFormat(format)
      && diagramOutputFormat != DiagramOutputFormat.htmlx;
    return supportsOutputFormat;
  }

}
