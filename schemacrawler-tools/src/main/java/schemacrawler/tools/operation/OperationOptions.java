/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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


import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.datatext.DataTextFormatOptions;

/**
 * Operator options.
 * 
 * @author Sualeh Fatehi
 */
public final class OperationOptions
  extends DataTextFormatOptions
{

  private static final long serialVersionUID = -7977434852526746391L;

  private Operation operation;

  /**
   * Operator options, defaults.
   */
  public OperationOptions()
  {
    this(null, null, (Operation) null);
  }

  /**
   * Operator options from properties. Constructor.
   * 
   * @param outputOptions
   *        Output options
   * @param operation
   *        Operation
   * @param config
   *        Config
   */
  public OperationOptions(final Config config,
                          final OutputOptions outputOptions,
                          final Operation operation)
  {
    super(config, outputOptions, null);

    if (operation == null)
    {
      this.operation = Operation.count;
    }
    else if (operation != Operation.queryover)
    {
      this.operation = operation;
    }
    else
    {
      throw new IllegalArgumentException("No query specified for query over");
    }
  }

  /**
   * Operator options from properties. Constructor.
   * 
   * @param outputOptions
   *        Output options
   * @param queryName
   *        Query name
   * @param config
   *        Config
   */
  public OperationOptions(final Config config,
                          final OutputOptions outputOptions,
                          final String queryName)
  {
    super(config, outputOptions, queryName);

    operation = Operation.queryover;
  }

  /**
   * Gets the operation.
   * 
   * @return Operation.
   */
  public Operation getOperation()
  {
    return operation;
  }

  /**
   * Sets the operation.
   * 
   * @param operation
   *        Operation
   */
  public void setOperation(final Operation operation)
  {
    if (operation == null)
    {
      throw new IllegalArgumentException("Cannot set null operation");
    }
    this.operation = operation;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("OperatorOptions[");
    buffer.append("operation=").append(operation);
    buffer.append(", outputOptions=").append(getOutputOptions());
    buffer.append("]");
    return buffer.toString();
  }

}
