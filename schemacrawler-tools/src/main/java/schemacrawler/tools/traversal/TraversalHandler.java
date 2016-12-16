/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.tools.traversal;


import schemacrawler.schema.CrawlInfo;
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

  void handle(CrawlInfo crawlInfo)
    throws SchemaCrawlerException;

  void handle(DatabaseInfo databaseInfo)
    throws SchemaCrawlerException;

  void handle(JdbcDriverInfo jdbcDriverInfo)
    throws SchemaCrawlerException;

  void handle(SchemaCrawlerInfo schemaCrawlerInfo)
    throws SchemaCrawlerException;

  void handleHeaderEnd()
    throws SchemaCrawlerException;

  void handleHeaderStart()
    throws SchemaCrawlerException;

  void handleInfoEnd()
    throws SchemaCrawlerException;

  void handleInfoStart()
    throws SchemaCrawlerException;

}
