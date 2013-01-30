/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.base.BaseTextOptions;

/**
 * Operator options.
 * 
 * @author Sualeh Fatehi
 */
public final class OperationOptions
  extends BaseTextOptions
{

  private static final long serialVersionUID = -7977434852526746391L;

  private static final String SHOW_LOBS = SCHEMACRAWLER_FORMAT_PREFIX
                                          + "data.show_lobs";

  /**
   * Operator options, defaults.
   */
  public OperationOptions()
  {
    this(null);
  }

  /**
   * Operator options from properties. Constructor.
   * 
   * @param config
   *        Config
   */
  public OperationOptions(final Config config)
  {
    super(config);
    setShowLobs(getBooleanValue(config, SHOW_LOBS));
  }

  /**
   * Whether to show LOBs.
   * 
   * @return Whether to show LOBs.
   */
  public boolean isShowLobs()
  {
    return getBooleanValue(SHOW_LOBS);
  }

  /**
   * Whether to show LOBs.
   * 
   * @param showLobs
   *        Whether to show LOBs
   */
  public void setShowLobs(final boolean showLobs)
  {
    setBooleanValue(SHOW_LOBS, showLobs);
  }

}
