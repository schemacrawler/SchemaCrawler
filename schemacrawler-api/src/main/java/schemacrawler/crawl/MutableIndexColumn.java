package schemacrawler.crawl;


import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.IndexColumnSortSequence;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Table;

final class MutableIndexColumn
  extends AbstractDependantObject<Table>
  implements IndexColumn
{

  private static final long serialVersionUID = -6923211341742623556L;

  private final Column column;
  private final Index index;
  private int indexOrdinalPosition;
  private IndexColumnSortSequence sortSequence;

  MutableIndexColumn(final Index index, final MutableColumn column)
  {
    super(column.getParent(), column.getName());
    this.index = index;
    this.column = column;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    if (obj == null)
    {
      return -1;
    }

    final MutableIndexColumn other = (MutableIndexColumn) obj;
    int comparison = 0;

    if (comparison == 0)
    {
      comparison = indexOrdinalPosition - other.indexOrdinalPosition;
    }
    if (comparison == 0)
    {
      comparison = super.compareTo(other);
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getDecimalDigits()
   */
  @Override
  public int getDecimalDigits()
  {
    return column.getDecimalDigits();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#getDefaultValue()
   */
  @Override
  public String getDefaultValue()
  {
    return column.getDefaultValue();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.IndexColumn#getIndex()
   */
  @Override
  public Index getIndex()
  {
    return index;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.IndexColumn#getIndexOrdinalPosition()
   */
  @Override
  public int getIndexOrdinalPosition()
  {
    return indexOrdinalPosition;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getOrdinalPosition()
   */
  @Override
  public int getOrdinalPosition()
  {
    return column.getOrdinalPosition();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#getPrivilege(java.lang.String)
   */
  @Override
  public Privilege<Column> getPrivilege(final String name)
  {
    return column.getPrivilege(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#getPrivileges()
   */
  @Override
  public Privilege<Column>[] getPrivileges()
  {
    return column.getPrivileges();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#getReferencedColumn()
   */
  @Override
  public Column getReferencedColumn()
  {
    return column.getReferencedColumn();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getSize()
   */
  @Override
  public int getSize()
  {
    return column.getSize();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.IndexColumn#getSortSequence()
   */
  @Override
  public IndexColumnSortSequence getSortSequence()
  {
    return sortSequence;
  }

  @Override
  public ColumnDataType getType()
  {
    return column.getType();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getWidth()
   */
  @Override
  public String getWidth()
  {
    return column.getWidth();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#isNullable()
   */
  @Override
  public boolean isNullable()
  {
    return column.isNullable();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#isPartOfForeignKey()
   */
  @Override
  public boolean isPartOfForeignKey()
  {
    return column.isPartOfForeignKey();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#isPartOfPrimaryKey()
   */
  @Override
  public boolean isPartOfPrimaryKey()
  {
    return column.isPartOfPrimaryKey();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#isPartOfUniqueIndex()
   */
  @Override
  public boolean isPartOfUniqueIndex()
  {
    return column.isPartOfUniqueIndex();
  }

  void setIndexOrdinalPosition(final int indexOrdinalPosition)
  {
    this.indexOrdinalPosition = indexOrdinalPosition;
  }

  void setSortSequence(final IndexColumnSortSequence sortSequence)
  {
    this.sortSequence = sortSequence;
  }

}
