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

package schemacrawler.tools.text.operation;


import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.base.BaseTextOptionsBuilder;

/**
 * Operator options.
 *
 * @author Sualeh Fatehi
 */
public final class OperationOptionsBuilder
  extends BaseTextOptionsBuilder<OperationOptionsBuilder, OperationOptions>
{
  private static final String SHOW_LOBS =
    SCHEMACRAWLER_FORMAT_PREFIX + "data.show_lobs";

  public static OperationOptionsBuilder builder()
  {
    return new OperationOptionsBuilder();
  }

  public static OperationOptionsBuilder builder(final OperationOptions options)
  {
    return new OperationOptionsBuilder().fromOptions(options);
  }

  public static OperationOptions newOperationOptions()
  {
    return new OperationOptionsBuilder().toOptions();
  }

  public static OperationOptions newOperationOptions(final Config config)
  {
    return new OperationOptionsBuilder()
      .fromConfig(config)
      .toOptions();
  }

  protected boolean isShowLobs;

  private OperationOptionsBuilder()
  {
    // Set default values, if any
  }

  @Override
  public OperationOptionsBuilder fromConfig(final Config map)
  {
    if (map == null)
    {
      return this;
    }
    super.fromConfig(map);

    final Config config = new Config(map);
    isShowLobs = config.getBooleanValue(SHOW_LOBS, false);

    return this;
  }

  @Override
  public OperationOptionsBuilder fromOptions(final OperationOptions options)
  {
    if (options == null)
    {
      return this;
    }
    super.fromOptions(options);

    isShowLobs = options.isShowLobs();

    return this;
  }

  public OperationOptionsBuilder showLobs()
  {
    return showLobs(true);
  }

  /**
   * Show LOB data, or not.
   *
   * @param value
   *   Whether to show LOB data.
   * @return Builder
   */
  public OperationOptionsBuilder showLobs(final boolean value)
  {
    isShowLobs = value;
    return this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = super.toConfig();
    config.setBooleanValue(SHOW_LOBS, isShowLobs);
    return config;
  }

  @Override
  public OperationOptions toOptions()
  {
    return new OperationOptions(this);
  }

}
