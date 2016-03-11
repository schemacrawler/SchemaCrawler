/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
import static sf.util.Utility.isBlank;

import java.sql.Connection;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.utility.Query;

public class LinterCatalogSql
  extends BaseLinter
{

  private String message;
  private String sql;

  @Override
  public String getSummary()
  {
    return message;
  }

  @Override
  protected void configure(final Config config)
  {
    requireNonNull(config, "No configuration provided");

    message = config.getStringValue("message", null);
    if (isBlank(message))
    {
      throw new IllegalArgumentException("No message provided");
    }

    sql = config.getStringValue("sql", null);
    if (isBlank(sql))
    {
      throw new IllegalArgumentException("No SQL provided");
    }
  }

  @Override
  protected void lint(final Table table, final Connection connection)
    throws SchemaCrawlerException
  {
    // No-op, since the actual linting is done in the start method
  }

  @Override
  protected void start(final Connection connection)
    throws SchemaCrawlerException
  {
    super.start(connection);

    if (isBlank(sql))
    {
      return;
    }

    requireNonNull(connection, "No connection provided");

    final Query query = new Query(message, sql);
    final Object queryResult = query.executeForScalar(connection);
    if (queryResult != null)
    {
      addCatalogLint(getSummary() + " " + queryResult, true);
    }
  }

}
