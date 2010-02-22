package schemacrawler.tools.analysis;


import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

public class LinterTableWithSingleColumn
  extends BaseLinter<Table>
{

  public void lint(final Table table)
  {
    if (table != null)
    {
      final Column[] columns = table.getColumns();
      if (columns.length <= 1)
      {
        addLint(table, new Lint("table has single column", Boolean.TRUE)
        {

          private static final long serialVersionUID = 2580606298217022285L;

          @Override
          public String getLintValueAsString()
          {
            return getLintValue().toString();
          }
        });
      }
    }
  }

}
