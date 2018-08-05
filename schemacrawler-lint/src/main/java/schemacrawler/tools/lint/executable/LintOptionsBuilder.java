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
package schemacrawler.tools.lint.executable;


import static sf.util.Utility.isBlank;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.base.BaseTextOptionsBuilder;

public final class LintOptionsBuilder
  extends BaseTextOptionsBuilder<LintOptionsBuilder, LintOptions>
{

  private static final String CLI_LINTER_CONFIGS = "linterconfigs";
  private static final String LINTER_CONFIGS = SCHEMACRAWLER_FORMAT_PREFIX
                                               + CLI_LINTER_CONFIGS;

  public static LintOptionsBuilder builder()
  {
    return new LintOptionsBuilder();
  }

  public static LintOptionsBuilder builder(final LintOptions options)
  {
    return new LintOptionsBuilder().fromOptions(options);
  }

  public static LintOptions newLintOptions()
  {
    return new LintOptionsBuilder().toOptions();
  }

  public static LintOptions newLintOptions(final Config config)
  {
    return new LintOptionsBuilder().fromConfig(config).toOptions();
  }

  protected String linterConfigs;

  private LintOptionsBuilder()
  {
    linterConfigs = "";
  }

  @Override
  public LintOptionsBuilder fromConfig(final Config map)
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
      linterConfigs = config.getStringValue(CLI_LINTER_CONFIGS, "");
    }
    else
    {
      // Otherwise, take option from SchemaCrawler configuration file
      linterConfigs = config.getStringValue(LINTER_CONFIGS, "");
    }

    return this;
  }

  @Override
  public LintOptionsBuilder fromOptions(final LintOptions options)
  {
    if (options == null)
    {
      return this;
    }
    super.fromOptions(options);

    linterConfigs = options.getLinterConfigs();

    return this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = super.toConfig();
    config.setStringValue(LINTER_CONFIGS, linterConfigs);
    return config;
  }

  @Override
  public LintOptions toOptions()
  {
    return new LintOptions(this);
  }

  /**
   * Whether to show LOBs.
   */
  public LintOptionsBuilder withLinterConfigs(final String linterConfigs)
  {
    if (isBlank(linterConfigs))
    {
      this.linterConfigs = "";
    }
    else
    {
      this.linterConfigs = linterConfigs;
    }
    return this;
  }

}
