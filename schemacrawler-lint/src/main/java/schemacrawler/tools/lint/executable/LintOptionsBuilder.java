/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
package schemacrawler.tools.lint.executable;


import java.util.Map;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.base.BaseTextOptionsBuilder;

public class LintOptionsBuilder
  extends BaseTextOptionsBuilder<LintOptions>
{

  private static final String CLI_LINTER_CONFIGS = "linterconfigs";
  private static final String LINTER_CONFIGS = SCHEMACRAWLER_FORMAT_PREFIX
                                               + CLI_LINTER_CONFIGS;

  public LintOptionsBuilder()
  {
    super(new LintOptions());
  }

  @Override
  public LintOptionsBuilder fromConfig(final Map<String, String> map)
  {
    if (map == null)
    {
      return this;
    }
    super.fromConfig(map);

    final Config config = new Config(map);
    if (config.containsKey(CLI_LINTER_CONFIGS))
    {
      // Honor command-line option first
      options.setLinterConfigs(config.getStringValue(CLI_LINTER_CONFIGS, ""));
    }
    else
    {
      // Otherwise, take option from SchemaCrawler configuration file
      options.setLinterConfigs(config.getStringValue(LINTER_CONFIGS, ""));
    }

    return this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = super.toConfig();
    config.setStringValue(LINTER_CONFIGS, options.getLinterConfigs());
    return config;
  }

  /**
   * Whether to show LOBs.
   */
  public LintOptionsBuilder withLinterConfigs(final String linterConfigs)
  {
    options.setLinterConfigs(linterConfigs);
    return this;
  }

}
