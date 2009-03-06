package schemacrawler.crawl;


import schemacrawler.schema.Column;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexColumnSortSequence;

final class MutableIndexColumn
  extends MutableColumn
  implements IndexColumn
{

  private static final long serialVersionUID = -6923211341742623556L;

  private final Index index;
  private IndexColumnSortSequence sortSequence;

  public IndexColumnSortSequence getSortSequence()
  {
    return sortSequence;
  }

  public MutableIndexColumn(Index index, Column column)
  {
    super(column.getParent(), column.getName());
    this.index = index;
    // 
    setDefaultValue(column.getDefaultValue());
    setPartOfPrimaryKey(column.isPartOfPrimaryKey());
    setPartOfUniqueIndex(column.isPartOfUniqueIndex());
    setReferencedColumn((MutableColumn) column.getReferencedColumn());
    //
    setDecimalDigits(column.getDecimalDigits());
    setNullable(column.isNullable());
    setOrdinalPosition(column.getOrdinalPosition());
    setSize(column.getSize());
    setType(column.getType());
  }

  public void setSortSequence(IndexColumnSortSequence sortSequence)
  {
    this.sortSequence = sortSequence;
  }

  public Index getIndex()
  {
    return index;
  }

}
