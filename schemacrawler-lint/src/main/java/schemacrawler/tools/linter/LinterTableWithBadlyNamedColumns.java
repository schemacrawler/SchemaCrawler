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
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.BaseLinter;

public class LinterTableWithBadlyNamedColumns
  extends BaseLinter
{

  private InclusionRule columnNames;

  @Override
  public String getSummary()
  {
    return "badly named column";
  }

  @Override
  protected void configure(final Config config)
  {
    requireNonNull(config, "No configuration provided");

    final String badColumnNames = config.getStringValue("bad-column-names",
                                                        null);
    if (isBlank(badColumnNames))
    {
      columnNames = new IncludeAll();
    }
    else
    {
      columnNames = new RegularExpressionInclusionRule(badColumnNames);
    }
  }

  @Override
  protected void lint(final Table table, final Connection connection)
    throws SchemaCrawlerException
  {
    requireNonNull(table, "No table provided");

    final List<Column> badlyNamedColumns = findBadlyNamedColumns(table
      .getColumns());
    for (final Column column: badlyNamedColumns)
    {
      addTableLint(table, getSummary(), column);
    }
  }

  private List<Column> findBadlyNamedColumns(final List<Column> columns)
  {
    final List<Column> badlyNamedColumns = new ArrayList<>();
    if (columnNames == null)
    {
      return badlyNamedColumns;
    }

    for (final Column column: columns)
    {
      if (columnNames.test(column.getFullName()))
      {
        badlyNamedColumns.add(column);
      }
    }
    return badlyNamedColumns;
  }

}
