/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
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

package schemacrawler.tools.text.operation;


import java.util.Map;

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
  public OperationOptionsBuilder fromConfig(final Map<String, String> map)
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
