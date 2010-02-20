package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import sf.util.Utility;

public class LinterTableWithNullIntendedColumns
  extends BaseLinter<Table>
{

  public void lint(final Table table)
  {
    if (table != null)
    {
      final Column[] nullDefaultValueMayBeIntendedColumns = findNullDefaultValueMayBeIntendedColumns(table
        .getColumns());
      if (nullDefaultValueMayBeIntendedColumns.length > 0)
      {
        addLint(table, new Lint("columns where NULL may be intended",
                                nullDefaultValueMayBeIntendedColumns));
      }
    }
  }

  private Column[] findNullDefaultValueMayBeIntendedColumns(final Column[] columns)
  {
    final List<Column> nullDefaultValueMayBeIntendedColumns = new ArrayList<Column>();
    for (final Column column: columns)
    {
      final String columnDefaultValue = column.getDefaultValue();
      if (!Utility.isBlank(columnDefaultValue)
          && columnDefaultValue.trim().equalsIgnoreCase("NULL"))
      {
        nullDefaultValueMayBeIntendedColumns.add(column);
      }
    }
    return nullDefaultValueMayBeIntendedColumns
      .toArray(new Column[nullDefaultValueMayBeIntendedColumns.size()]);
  }

}
