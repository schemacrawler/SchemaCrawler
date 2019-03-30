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

package schemacrawler.tools.commandline;


import java.util.Objects;
import java.util.logging.Level;

import schemacrawler.schemacrawler.*;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerOptionsParser
  extends BaseOptionsParser<SchemaCrawlerOptions>
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawlerOptionsParser.class.getName());

  private final SchemaCrawlerOptionsBuilder optionsBuilder;

  public SchemaCrawlerOptionsParser(final SchemaCrawlerOptionsBuilder optionsBuilder,
                                    final Config config)
  {
    super(config);
    normalizeOptionName("infolevel", "i");

    this.optionsBuilder = Objects.requireNonNull(optionsBuilder);
  }

  @Override
  public SchemaCrawlerOptions getOptions()
    throws SchemaCrawlerException
  {

    // Load schema info level configuration from config, and override
    // with command-line options
    final SchemaInfoLevelBuilder schemaInfoLevelBuilder = SchemaInfoLevelBuilder
      .builder().fromConfig(config);
    if (config.hasValue("infolevel"))
    {
      final InfoLevel infoLevel = config
        .getEnumValue("infolevel", InfoLevel.standard);
      schemaInfoLevelBuilder.withInfoLevel(infoLevel);
      consumeOption("infolevel");
    }
    else
    {
      // Default to standard infolevel
      schemaInfoLevelBuilder.withInfoLevel(InfoLevel.standard);
    }
    optionsBuilder.withSchemaInfoLevel(schemaInfoLevelBuilder);

    return null;
  }

  private void logOverride(final String inclusionRuleName,
                           final InclusionRule schemaInclusionRule)
  {
    LOGGER.log(Level.INFO,
               new StringFormat(
                 "Overriding %s inclusion rule from command-line to %s",
                 inclusionRuleName,
                 schemaInclusionRule));
  }

}
