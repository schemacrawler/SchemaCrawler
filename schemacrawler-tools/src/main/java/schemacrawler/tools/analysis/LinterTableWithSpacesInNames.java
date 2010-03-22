package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import sf.util.ObjectToString;

public class LinterTableWithSpacesInNames
  extends BaseLinter<Table>
{

  public void lint(final Table table)
  {
    if (table != null)
    {
      final List<String> spacesInNamesList = findColumnsWithSpacesInNames(table
        .getColumns());
      final String tableName = table.getName();
      if (tableName.contains(" "))
      {
        spacesInNamesList.add(0, "[" + tableName + "]");
      }
      if (!spacesInNamesList.isEmpty())
      {
        final String[] spacesInNames = spacesInNamesList
          .toArray(new String[spacesInNamesList.size()]);
        addLint(table, new Lint("spaces in names", spacesInNames)
        {

          private static final long serialVersionUID = 4306137113072609086L;

          @Override
          public String getLintValueAsString()
          {
            return ObjectToString.toString(spacesInNames);
          }
        });
      }
    }
  }

  private List<String> findColumnsWithSpacesInNames(final Column[] columns)
  {
    final List<String> columnsWithSpacesInNames = new ArrayList<String>();
    for (final Column column: columns)
    {
      final String columnName = column.getName();
      if (columnName.contains(" "))
      {
        columnsWithSpacesInNames.add("[" + columnName + "]");
      }
    }
    return columnsWithSpacesInNames;
  }

}
