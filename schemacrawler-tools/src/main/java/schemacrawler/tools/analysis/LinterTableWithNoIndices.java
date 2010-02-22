package schemacrawler.tools.analysis;


import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;

public class LinterTableWithNoIndices
  extends BaseLinter<Table>
{

  public void lint(final Table table)
  {
    if (table != null && !(table instanceof View))
    {
      final Index[] indices = table.getIndices();
      if (table.getPrimaryKey() == null && indices.length == 0)
      {
        addLint(table, new Lint("no indices", Boolean.TRUE)
        {

          private static final long serialVersionUID = -9070658409181468265L;

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
