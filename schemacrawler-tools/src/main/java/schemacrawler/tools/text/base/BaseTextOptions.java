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

package schemacrawler.tools.text.base;


import schemacrawler.schemacrawler.BaseConfigOptions;
import schemacrawler.schemacrawler.Config;

public abstract class BaseTextOptions
  extends BaseConfigOptions
{

  private static final long serialVersionUID = -8133661515343358712L;

  protected static final String SCHEMACRAWLER_FORMAT_PREFIX = "schemacrawler.format.";

  private static final String NO_HEADER = SCHEMACRAWLER_FORMAT_PREFIX
                                          + "no_header";
  private static final String NO_FOOTER = SCHEMACRAWLER_FORMAT_PREFIX
                                          + "no_footer";
  private static final String NO_INFO = SCHEMACRAWLER_FORMAT_PREFIX + "no_info";
  private static final String APPEND_OUTPUT = SCHEMACRAWLER_FORMAT_PREFIX
                                              + "append_output";

  private static final String SHOW_UNQUALIFIED_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                       + "show_unqualified_names";

  protected BaseTextOptions()
  {
    this(null);
  }

  protected BaseTextOptions(final Config config)
  {
    setNoFooter(getBooleanValue(config, NO_FOOTER));
    setNoHeader(getBooleanValue(config, NO_HEADER));
    setNoInfo(getBooleanValue(config, NO_INFO));
    setAppendOutput(getBooleanValue(config, APPEND_OUTPUT));

    setShowUnqualifiedNames(getBooleanValue(config, SHOW_UNQUALIFIED_NAMES));
  }

  public boolean isAppendOutput()
  {
    return getBooleanValue(APPEND_OUTPUT);
  }

  /**
   * Whether to print footers.
   * 
   * @return Whether to print footers
   */
  public boolean isNoFooter()
  {
    return getBooleanValue(NO_FOOTER);
  }

  /**
   * Whether to print headers.
   * 
   * @return Whether to print headers
   */
  public boolean isNoHeader()
  {
    return getBooleanValue(NO_HEADER);
  }

  /**
   * Whether to print information.
   * 
   * @return Whether to print information
   */
  public boolean isNoInfo()
  {
    return getBooleanValue(NO_INFO);
  }

  public boolean isShowUnqualifiedNames()
  {
    return getBooleanValue(SHOW_UNQUALIFIED_NAMES);
  }

  public void setAppendOutput(final boolean appendOutput)
  {
    setBooleanValue(APPEND_OUTPUT, appendOutput);
  }

  /**
   * Whether to print footers.
   * 
   * @param noFooter
   *        Whether to print footers
   */
  public void setNoFooter(final boolean noFooter)
  {
    setBooleanValue(NO_FOOTER, noFooter);
  }

  /**
   * Whether to print headers.
   * 
   * @param noHeader
   *        Whether to print headers
   */
  public void setNoHeader(final boolean noHeader)
  {
    setBooleanValue(NO_HEADER, noHeader);
  }

  /**
   * Whether to print information.
   * 
   * @param noInfo
   *        Whether to print information
   */
  public void setNoInfo(final boolean noInfo)
  {
    setBooleanValue(NO_INFO, noInfo);
  }

  public void setShowUnqualifiedNames(final boolean showUnqualifiedNames)
  {
    setBooleanValue(SHOW_UNQUALIFIED_NAMES, showUnqualifiedNames);
  }

}
