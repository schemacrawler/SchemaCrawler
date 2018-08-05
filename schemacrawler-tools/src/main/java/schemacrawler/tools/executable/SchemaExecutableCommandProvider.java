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


import static sf.util.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextRenderer;

public final class SchemaExecutableCommandProvider
  extends ExecutableCommandProvider
{

  private static final Collection<String> supportedCommands = supportedCommands();

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
  public Collection<String> getSupportedCommands()
  {
    return supportedCommands();
  }

  @Override
  public SchemaCrawlerCommand newSchemaCrawlerCommand(final String command)
    throws SchemaCrawlerException
  {
    final SchemaCrawlerCommand scCommand = new SchemaTextRenderer(command);
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
    final String outputFormatValue = outputOptions.getOutputFormatValue();
    return supportedCommands.contains(command)
           && (isBlank(outputFormatValue)
               || TextOutputFormat.isSupportedFormat(outputFormatValue));
  }

}
