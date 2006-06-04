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

package schemacrawler.tools.operation;


import java.util.Properties;

import schemacrawler.tools.BaseToolOptions;
import schemacrawler.tools.OutputOptions;

/**
 * Operator options.
 * 
 * @author sfatehi
 */
public final class OperatorOptions
  extends BaseToolOptions
{

  private static final long serialVersionUID = -7977434852526746391L;

  private final Operation operation;
  private final String query;

  /**
   * Operator options, defaults.
   */
  public OperatorOptions()
  {
    this(null, null, null, null);
  }

  /**
   * Operator options from properties. Constructor.
   * 
   * @param config
   *          Properties
   * @param outputOptions
   *          Output options
   * @param operation
   *          Operation
   * @param query
   *          Query
   */
  public OperatorOptions(final Properties config,
                         final OutputOptions outputOptions,
                         final Operation operation, final String query)
  {
    super(outputOptions);

    if (operation == null)
    {
      this.operation = Operation.valueOf("count");
    }
    else
    {
      this.operation = operation;
    }
    this.query = query;

    // Config is not read
  }

  String getQuery()
  {
    return query;
  }

  Operation getOperation()
  {
    return operation;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("OperatorOptions[");
    buffer.append("operation=").append(operation);
    buffer.append(", query=").append(query);
    buffer.append(", outputOptions=").append(getOutputOptions());
    buffer.append("]");
    return buffer.toString();
  }

}
