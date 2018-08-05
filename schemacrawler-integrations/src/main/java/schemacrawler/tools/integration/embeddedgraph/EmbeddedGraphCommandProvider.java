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
package schemacrawler.tools.integration.embeddedgraph;


import static sf.util.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.ExecutableCommandProvider;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public final class EmbeddedGraphCommandProvider
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

  public EmbeddedGraphCommandProvider()
  {
    super(supportedCommands, "");
  }

  @Override
  public Collection<String> getSupportedCommands()
  {
    return supportedCommands();
  }

  @Override
  public SchemaCrawlerCommand newSchemaCrawlerCommand(final String command)
    throws SchemaCrawlerException
  {
    final SchemaCrawlerCommand scCommand = new EmbeddedGraphRenderer(command);
    return scCommand;
  }

  @Override
  public boolean supportsSchemaCrawlerCommand(final String command,
                                              final SchemaCrawlerOptions schemaCrawlerOptions,
                                              final OutputOptions outputOptions)
  {
    if (isBlank(command) || outputOptions == null)
    {
      return false;
    }
    final String format = outputOptions.getOutputFormatValue();
    final GraphOutputFormat graphOutputFormat = GraphOutputFormat
      .fromFormat(format);
    final boolean supportsSchemaCrawlerCommand = supportedCommands
      .contains(command) && graphOutputFormat == GraphOutputFormat.htmlx;
    return supportsSchemaCrawlerCommand;
  }

}
