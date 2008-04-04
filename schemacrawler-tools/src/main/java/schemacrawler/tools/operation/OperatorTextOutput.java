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


import java.sql.Connection;

import schemacrawler.execute.DataHandler;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.util.PlainTextFormattingHelper;
import sf.util.Utilities;

/**
 * 
 */
final class OperatorTextOutput
  extends BaseOperator
{

  /**
   * Constructs a new table dropper.
   * 
   * @param operation
   *        Operation to perform.
   * @param connection
   *        Database connection to use
   */
  OperatorTextOutput(final OperationOptions options,
                     final Query query,
                     final Connection connection,
                     final DataHandler dataHandler)
    throws SchemaCrawlerException
  {
    super(options,
          query,
          connection,
          dataHandler,
          new PlainTextFormattingHelper(options.getOutputOptions()
            .getOutputFormat().name()));
  }

  /**
   * @see BaseOperator#end()
   */
  @Override
  public void end()
    throws SchemaCrawlerException
  {

    if (!getNoFooter())
    {
      out.println(Utilities.NEWLINE + getTableCount() + " tables");
    }
    super.end();
  }

}
