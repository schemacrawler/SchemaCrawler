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

package schemacrawler.tools;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.ObjectToString;

/**
 * A SchemaCrawler tools executable unit.
 * 
 * @author Sualeh Fatehi
 * @param <O>
 *        Tool-specific options for execution.
 */
public abstract class Executable<O extends ToolOptions>
{

  protected SchemaCrawlerOptions schemaCrawlerOptions;
  protected O toolOptions;

  /**
   * Creates an executable with some default options.
   */
  protected Executable()
  {
    schemaCrawlerOptions = new SchemaCrawlerOptions();
  }

  /**
   * Executes main functionality for SchemaCrawler.
   * 
   * @param connection
   *        Database connection
   * @throws Exception
   *         On an exception
   */
  public abstract void execute(Connection connection)
    throws Exception;

  /**
   * Gets the schema crawler options.
   * 
   * @return SchemaCrawlerOptions
   */
  public final SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  /**
   * Gets the tool options.
   * 
   * @return Tool options
   */
  public final O getToolOptions()
  {
    return toolOptions;
  }

  /**
   * Sets the schema crawler options.
   * 
   * @param schemaCrawlerOptions
   *        SchemaCrawlerOptions
   */
  public final void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

  /**
   * Gets the tool options.
   * 
   * @param toolOptions
   *        Tool options
   */
  public final void setToolOptions(final O toolOptions)
  {
    this.toolOptions = toolOptions;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public final String toString()
  {
    final Map<String, Object> fields = new HashMap<String, Object>();
    fields.put("schemaCrawlerOptions", schemaCrawlerOptions);
    fields.put("toolOptions", toolOptions);
    return ObjectToString.toString(this, fields);
  }

}
