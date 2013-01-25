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
package schemacrawler.tools.traversal;


import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface SchemaTraversalHandler
  extends TraversalHandler
{

  void handle(final ColumnDataType columnDataType)
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param routine
   *        Routine metadata.
   */
  void handle(final Routine routine)
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param synonym
   *        Synonym metadata.
   */
  void handle(final Synonym synonym)
    throws SchemaCrawlerException;

  /**
   * Provides information on the database schema.
   * 
   * @param table
   *        Table metadata.
   */
  void handle(final Table table)
    throws SchemaCrawlerException;

  void handleColumnDataTypesEnd()
    throws SchemaCrawlerException;

  void handleColumnDataTypesStart()
    throws SchemaCrawlerException;

  void handleRoutinesEnd()
    throws SchemaCrawlerException;

  void handleRoutinesStart()
    throws SchemaCrawlerException;

  void handleSynonymsEnd()
    throws SchemaCrawlerException;

  void handleSynonymsStart()
    throws SchemaCrawlerException;

  void handleTablesEnd()
    throws SchemaCrawlerException;

  void handleTablesStart()
    throws SchemaCrawlerException;

}
