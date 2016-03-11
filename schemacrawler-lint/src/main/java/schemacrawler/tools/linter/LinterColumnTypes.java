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

import java.sql.Connection;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.BaseLinter;
import sf.util.Multimap;

public class LinterColumnTypes
  extends BaseLinter
{

  private Multimap<String, ColumnDataType> columnTypes;

  @Override
  public String getSummary()
  {
    return "column with same name but different data types";
  }

  @Override
  protected void end(final Connection connection)
    throws SchemaCrawlerException
  {
    requireNonNull(columnTypes, "Not initialized");

    for (final Entry<String, List<ColumnDataType>> entry: columnTypes
      .entrySet())
    {
      final SortedSet<ColumnDataType> currentColumnTypes = new TreeSet<>(entry
        .getValue());
      if (currentColumnTypes.size() > 1)
      {
        addCatalogLint(getSummary(), entry.getKey() + " " + currentColumnTypes);
      }
    }

    columnTypes = null;

    super.end(connection);
  }

  @Override
  protected void lint(final Table table, final Connection connection)
  {
    requireNonNull(table, "No table provided");
    requireNonNull(columnTypes, "Not initialized");

    for (final Column column: getColumns(table))
    {
      columnTypes.add(column.getName(), column.getColumnDataType());
    }
  }

  @Override
  protected void start(final Connection connection)
    throws SchemaCrawlerException
  {
    super.start(connection);

    columnTypes = new Multimap<>();
  }

}
