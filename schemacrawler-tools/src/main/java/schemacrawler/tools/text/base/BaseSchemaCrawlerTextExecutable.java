/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler.tools.text.base;


import java.sql.Connection;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public abstract class BaseSchemaCrawlerTextExecutable
  extends BaseExecutable
{

  private static final long serialVersionUID = -6824567755397315920L;

  protected BaseSchemaCrawlerTextExecutable(final String command)
  {
    super(command);
  }

  @Override
  protected final void executeOn(final Database database,
                                 final Connection connection)
    throws Exception
  {
    final DatabaseTraversalHandler handler = getDatabaseTraversalHandler(connection);
    final DatabaseTraverser traverser = new DatabaseTraverser(database);
    traverser.traverse(handler);
  }

  protected abstract DatabaseTraversalHandler getDatabaseTraversalHandler(Connection connection)
    throws SchemaCrawlerException;

}
