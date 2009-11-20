/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.options;


/**
 * Tool options.
 * 
 * @author Sualeh Fatehi
 */
public abstract class BaseToolOptions
  implements ToolOptions
{

  private static final long serialVersionUID = -2072130838813852782L;

  private OutputOptions outputOptions;

  /**
   * Data text formatting options from properties.
   * 
   * @param outputOptions
   *        Output options
   */
  public BaseToolOptions(final OutputOptions outputOptions)
  {
    if (outputOptions != null)
    {
      this.outputOptions = outputOptions;
    }
    else
    {
      this.outputOptions = new OutputOptions();
    }
  }

  /**
   * Get output options.
   * 
   * @return Output options
   */
  public OutputOptions getOutputOptions()
  {
    return outputOptions;
  }

  public abstract boolean isPrintVerboseDatabaseInfo();

  /**
   * Set output options.
   * 
   * @param outputOptions
   *        Output options
   */
  public void setOutputOptions(final OutputOptions outputOptions)
  {
    if (outputOptions == null)
    {
      throw new IllegalArgumentException("Cannot set null OutputOptions");
    }
    this.outputOptions = outputOptions;
  }

}
