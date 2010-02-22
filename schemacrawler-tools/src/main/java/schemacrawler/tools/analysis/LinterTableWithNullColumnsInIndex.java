package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import sf.util.ObjectToString;

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
          nullableColumnsInUniqueIndex)
        {

          private static final long serialVersionUID = -1954217739621236510L;

          @Override
          public String getLintValueAsString()
          {
            final List<String> columnNames = new ArrayList<String>();
            for (final Column column: nullableColumnsInUniqueIndex)
            {
              columnNames.add(column.getName());
            }
            return ObjectToString.toString(columnNames);
          }
        });
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
