/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

package schemacrawler.tools.datatext;


import java.util.Properties;

import schemacrawler.tools.BaseToolOptions;
import schemacrawler.tools.OutputOptions;

/**
 * Data text formatting options.
 * 
 * @author sfatehi
 */
public final class DataTextFormatOptions
  extends BaseToolOptions
{

  private static final long serialVersionUID = -3031922069348696647L;

  private static final String SHOW_LOBS = "schemacrawler.data.show_lobs";
  private static final String MERGE_ROWS = "schemacrawler.data.merge_rows";
  private final boolean mergeRows;
  private final boolean showLobs;

  /**
   * Data text formatting options, defaults.
   */
  public DataTextFormatOptions()
  {
    this(null, null);
  }

  /**
   * Data text formatting options from properties.
   * 
   * @param config
   *          Properties
   * @param outputOptions
   *          Page options
   */
  public DataTextFormatOptions(final Properties config,
                               final OutputOptions outputOptions)
  {
    super(outputOptions);

    if (config == null)
    {
      this.mergeRows = false;
      this.showLobs = false;
    }
    else
    {
      this.mergeRows = getBooleanProperty(MERGE_ROWS, config);
      this.showLobs = getBooleanProperty(SHOW_LOBS, config);
    }
  }

  boolean isMergeRows()
  {
    return mergeRows;
  }

  boolean isShowLobs()
  {
    return showLobs;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("DataTextFormatOptions[");
    buffer.append("mergeRows=").append(mergeRows);
    buffer.append(", showLobs=").append(showLobs);
    buffer.append(", outputOptions=").append(getOutputOptions());
    buffer.append("]");
    return buffer.toString();
  }

}
