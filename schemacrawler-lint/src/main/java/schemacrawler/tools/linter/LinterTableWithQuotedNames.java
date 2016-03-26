/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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
