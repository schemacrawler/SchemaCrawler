/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
package schemacrawler.tools.linter;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.utility.Query;

public class LinterTableEmpty
  extends BaseLinter
{

  private static final Logger LOGGER = Logger
    .getLogger(LinterTableEmpty.class.getName());

  @Override
  public String getSummary()
  {
    return "empty table";
  }

  @Override
  protected void configure(final Config config)
  {
    super.configure(config);
    setSeverity(LintSeverity.low);
  }

  @Override
  protected void lint(final Table table, final Connection connection)
  {
    requireNonNull(table, "No table provided");
    requireNonNull(connection, "No connection provided");

    final Query query = new Query("Count", "SELECT COUNT(*) FROM ${table}");
    try
    {
      final long count = query.executeForLong(connection, table);
      if (count == 0)
      {
        addLint(table, getSummary(), true);
      }
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING, "Could not get count for " + table, e);
    }
  }

}
