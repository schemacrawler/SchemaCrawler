package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import sf.util.ObjectToString;

public class LinterTableWithSpacesInColumnNames
  extends BaseLinter<Table>
{

  public void lint(final Table table)
  {
    if (table != null)
    {
      final Column[] columnsWithSpacesInNames = findColumnsWithSpacesInNames(table
        .getColumns());
      if (columnsWithSpacesInNames.length > 0)
      {
        addLint(table, new Lint("columns with spaces in name",
          columnsWithSpacesInNames)
        {

          private static final long serialVersionUID = 4306137113072609086L;

          @Override
          public String getLintValueAsString()
          {
            final List<String> columnNames = new ArrayList<String>();
            for (final Column column: columnsWithSpacesInNames)
            {
              columnNames.add("[" + column.getName() + "]");
            }
            return ObjectToString.toString(columnNames);
          }
        });
      }
    }
  }

  private Column[] findColumnsWithSpacesInNames(final Column[] columns)
  {
    final List<Column> columnsWithSpacesInNames = new ArrayList<Column>();
    for (final Column column: columns)
    {
      final String columnName = column.getName();
      if (columnName.contains(" "))
      {
        columnsWithSpacesInNames.add(column);
      }
    }
    return columnsWithSpacesInNames.toArray(new Column[columnsWithSpacesInNames
      .size()]);
  }

}
