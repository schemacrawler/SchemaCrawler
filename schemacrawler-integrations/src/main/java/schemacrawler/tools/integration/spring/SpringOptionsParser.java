/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.integration.spring;


import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.BaseOptionsParser;

/**
 * Options for the command-line.
 *
 * @author sfatehi
 */
final class SpringOptionsParser
  extends BaseOptionsParser<SpringOptions>
{

  /**
   * Parses the command-line into options.
   *
   * @param args
   *        Command-line arguments
   */
  SpringOptionsParser(final Config config)
  {
    super(config);
    normalizeOptionName("context-file", "c");
    normalizeOptionName("executable", "x");
    normalizeOptionName("datasource", "d");
    normalizeOptionName("databaseoverrides", "m");
  }

  @Override
  protected SpringOptions getOptions()
  {
    final SpringOptions options = new SpringOptions();
    options.setContextFileName(config
      .getStringValue("context-file", "schemacrawler.context.xml"));
    options
      .setExecutableName(config.getStringValue("executable", "executable"));
    options
      .setDataSourceName(config.getStringValue("datasource", "datasource"));
    options.setDatabaseSpecificOverrideOptionsName(config
      .getStringValue("databaseoverrides", "databaseSpecificOverrideOptions"));

    consumeOption("context-file");
    consumeOption("executable");
    consumeOption("datasource");
    consumeOption("databaseoverrides");

    return options;
  }

}
