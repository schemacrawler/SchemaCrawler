package schemacrawler.crawl;


import schemacrawler.schema.*;

final class MutableIndexColumn
  extends AbstractDependantObject
  implements IndexColumn
{

  private static final long serialVersionUID = -6923211341742623556L;

  private final Column column;
  private final Index index;
  private int indexOrdinalPosition;
  private IndexColumnSortSequence sortSequence;

  MutableIndexColumn(final Index index, final Column column)
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
  public int getDecimalDigits()
  {
    return column.getDecimalDigits();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#getDefaultValue()
   */
  public String getDefaultValue()
  {
    return column.getDefaultValue();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.IndexColumn#getIndex()
   */
  public Index getIndex()
  {
    return index;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.IndexColumn#getIndexOrdinalPosition()
   */
  public int getIndexOrdinalPosition()
  {
    return indexOrdinalPosition;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.BaseColumn#getOrdinalPosition()
   */
  public int getOrdinalPosition()
  {
    return column.getOrdinalPosition();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#getPrivilege(java.lang.String)
   */
  public Privilege getPrivilege(final String name)
  {
    return column.getPrivilege(name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#getPrivileges()
   */
  public Privilege[] getPrivileges()
  {
    return column.getPrivileges();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#getReferencedColumn()
   */
  public Column getReferencedColumn()
  {
    return column.getReferencedColumn();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.BaseColumn#getSize()
   */
  public int getSize()
  {
    return column.getSize();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.IndexColumn#getSortSequence()
   */
  public IndexColumnSortSequence getSortSequence()
  {
    return sortSequence;
  }

  public ColumnDataType getType()
  {
    return column.getType();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.BaseColumn#getWidth()
   */
  public String getWidth()
  {
    return column.getWidth();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.BaseColumn#isNullable()
   */
  public boolean isNullable()
  {
    return column.isNullable();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfForeignKey()
   */
  public boolean isPartOfForeignKey()
  {
    return column.isPartOfForeignKey();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfPrimaryKey()
   */
  public boolean isPartOfPrimaryKey()
  {
    return column.isPartOfPrimaryKey();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isPartOfUniqueIndex()
   */
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
