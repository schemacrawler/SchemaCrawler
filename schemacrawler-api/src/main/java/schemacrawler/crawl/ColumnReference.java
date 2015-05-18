package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Column;

class ColumnReference
  extends DatabaseObjectReference<Column>
{

  private static final long serialVersionUID = 122669483681884924L;

  ColumnReference(final Column column)
  {
    super(requireNonNull(column, "No column provided"),
          new ColumnPartial(column));
  }

}
