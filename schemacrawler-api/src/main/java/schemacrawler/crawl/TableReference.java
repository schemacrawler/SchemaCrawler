package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Table;

class TableReference
  extends DatabaseObjectReference<Table>
{

  private static final long serialVersionUID = 8940800217960888019L;

  TableReference(final Table table)
  {
    super(requireNonNull(table, "No table provided"), new TablePartial(table));
  }

}
