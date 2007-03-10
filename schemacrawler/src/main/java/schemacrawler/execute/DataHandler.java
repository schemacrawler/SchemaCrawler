/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.execute;


import java.io.PrintWriter;
import java.sql.ResultSet;

/**
 * Handler for SQL executor.
 */
public interface DataHandler
{

  /**
   * Handles the begin of the execution.
   * 
   * @throws QueryExecutorException
   *         On an exception
   */
  void begin()
    throws QueryExecutorException;

  /**
   * Handles the end of the execution.
   * 
   * @throws QueryExecutorException
   *         On an exception
   */
  void end()
    throws QueryExecutorException;

  /**
   * Gets the output print writer.
   * 
   * @return Print writer
   */
  PrintWriter getPrintWriter();

  /**
   * Handles actual data.
   * 
   * @param rows
   *        Data from the execution.
   * @throws QueryExecutorException
   *         On an exception
   */
  void handleData(final ResultSet rows)
    throws QueryExecutorException;

  /**
   * Handles metadata information.
   * 
   * @param databaseInfo
   *        Database metadata.
   * @throws QueryExecutorException
   *         On an exception
   */
  void handleMetadata(final String databaseInfo)
    throws QueryExecutorException;

  /**
   * Handles metadata information.
   * 
   * @param title
   *        Execution title.
   * @throws QueryExecutorException
   *         On an exception
   */
  void handleTitle(final String title)
    throws QueryExecutorException;

}
