package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

public class LinterTableWithNullColumnsInIndex
  extends BaseLinter<Table>
{

  public void lint(final Table table)
  {
    if (table != null)
    {
      final Column[] nullableColumnsInUniqueIndex = findNullableColumnsInUniqueIndex(table
        .getColumns());
      if (nullableColumnsInUniqueIndex.length > 0)
      {
        addLint(table, new Lint("nullable columns in unique index",
                                nullableColumnsInUniqueIndex));
      }
    }
  }

  private Column[] findNullableColumnsInUniqueIndex(final Column[] columns)
  {
    final List<Column> nullableColumnsInUniqueIndex = new ArrayList<Column>();
    for (final Column column: columns)
    {
      if (column.isNullable() && column.isPartOfUniqueIndex())
      {
        nullableColumnsInUniqueIndex.add(column);
      }
    }
    return nullableColumnsInUniqueIndex
      .toArray(new Column[nullableColumnsInUniqueIndex.size()]);
  }

}
