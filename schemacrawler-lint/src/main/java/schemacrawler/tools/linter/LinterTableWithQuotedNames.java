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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.utility.Identifiers;

public class LinterTableWithQuotedNames
  extends BaseLinter
{

  @Override
  public String getSummary()
  {
    return "spaces in name, or reserved word";
  }

  @Override
  protected void lint(final Table table, final Connection connection)
    throws SchemaCrawlerException
  {
    requireNonNull(table, "No table provided");

    Identifiers identifiers;
    try
    {
      identifiers = Identifiers.identifiers().withConnection(connection)
        .build();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }

    final String tableName = table.getName();
    if (identifiers.isQuotedName(tableName))
    {
      addTableLint(table, getSummary());
    }

    final List<String> spacesInNamesList = findColumnsWithQuotedNames(table
      .getColumns(), identifiers);
    for (final String spacesInName: spacesInNamesList)
    {
      addTableLint(table, getSummary(), spacesInName);
    }
  }

  private List<String> findColumnsWithQuotedNames(final List<Column> columns,
                                                  final Identifiers identifiers)
  {
    final List<String> columnsWithQuotedNames = new ArrayList<>();
    for (final Column column: columns)
    {
      final String columnName = column.getName();
      if (identifiers.isQuotedName(columnName))
      {
        columnsWithQuotedNames.add(columnName);
      }
    }
    return columnsWithQuotedNames;
  }

}
