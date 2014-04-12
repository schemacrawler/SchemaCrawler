/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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

package schemacrawler.tools.offline;


import java.sql.Connection;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.executable.StagedExecutable;

/**
 * A SchemaCrawler tools executable unit.
 *
 * @author Sualeh Fatehi
 */
public class OfflineSnapshotExecutable
extends BaseExecutable
implements StagedExecutable
{

  protected OfflineSnapshotExecutable(final String command)
  {
    super(command);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.executable.Executable#execute(java.sql.Connection)
   */
  @Override
  public final void execute(final Connection connection)
      throws Exception
  {
    if (connection != null)
    {
      throw new SchemaCrawlerException("No connection should be provided");
    }

    final Database database = loadDatabase();

    executeOn(database, connection);
  }

  @Override
  public void executeOn(final Database database, final Connection connection)
      throws Exception
  {
    if (connection != null)
    {
      throw new SchemaCrawlerException("No connection should be provided");
    }

    // Create new SchemaCrawler executable
    final StagedExecutable executable = new SchemaCrawlerExecutable(command);
    executable.executeOn(database, connection);
  }

  private Database loadDatabase()
  {
    return null;
  }

}
