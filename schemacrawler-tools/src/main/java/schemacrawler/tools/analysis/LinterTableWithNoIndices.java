package schemacrawler.tools.analysis;


import schemacrawler.schema.Index;
import schemacrawler.schema.Table;

public class LinterTableWithNoIndices
  extends BaseLinter<Table>
{

  public void lint(final Table table)
  {
    if (table != null)
    {
      final Index[] indices = table.getIndices();
      if (indices.length == 0)
      {
        addLint(table, new Lint("table has no indices", Boolean.TRUE));
      }
    }
  }

}
