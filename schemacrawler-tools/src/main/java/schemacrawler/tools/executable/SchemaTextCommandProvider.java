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
package schemacrawler.tools.executable;

import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import schemacrawler.tools.text.schema.SchemaTextRenderer;

public final class SchemaTextCommandProvider extends BaseCommandProvider {

  SchemaTextCommandProvider() {
    super(CommandProviderUtility.schemaTextCommands());
  }

  @Override
  public SchemaTextRenderer newSchemaCrawlerCommand(final String command, final Config config) {
    final SchemaTextOptions schemaTextOptions =
        SchemaTextOptionsBuilder.builder().fromConfig(config).toOptions();

    final SchemaTextRenderer scCommand = new SchemaTextRenderer(command);
    scCommand.setCommandOptions(schemaTextOptions);
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return supportsOutputFormat(command, outputOptions, TextOutputFormat::isSupportedFormat);
  }
}
