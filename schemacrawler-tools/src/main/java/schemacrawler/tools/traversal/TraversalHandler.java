/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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


import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface TraversalHandler
{

  void begin()
    throws SchemaCrawlerException;

  void end()
    throws SchemaCrawlerException;

  void handle(DatabaseInfo databaseInfo)
    throws SchemaCrawlerException;

  void handle(JdbcDriverInfo jdbcDriverInfo)
    throws SchemaCrawlerException;

  void handle(SchemaCrawlerInfo schemaCrawlerInfo)
    throws SchemaCrawlerException;

  void handleInfoEnd()
    throws SchemaCrawlerException;

  void handleInfoStart()
    throws SchemaCrawlerException;

}
