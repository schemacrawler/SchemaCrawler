/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
  extends BaseTextOptionsBuilder<OperationOptions>
{

  private static final String SHOW_LOBS = SCHEMACRAWLER_FORMAT_PREFIX
                                          + "data.show_lobs";

  /**
   * Operator options, defaults.
   */
  public OperationOptionsBuilder()
  {
    super(new OperationOptions());
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
    options.setShowLobs(config.getBooleanValue(SHOW_LOBS, false));

    return this;
  }

  /**
   * Whether to show LOBs.
   */
  public OperationOptionsBuilder showLobs()
  {
    options.setShowLobs(true);
    return this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = super.toConfig();
    config.setBooleanValue(SHOW_LOBS, options.isShowLobs());
    return config;
  }

}
