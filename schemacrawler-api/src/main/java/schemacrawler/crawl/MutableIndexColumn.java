package schemacrawler.crawl;


import java.util.Collection;
import java.util.Optional;

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

  MutableIndexColumn(final Index index, final Column column)
  {
    super(new TableReference(column.getParent()), column.getName());
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

    int comparison = 0;

    if (obj instanceof MutableIndexColumn)
    {
      final MutableIndexColumn other = (MutableIndexColumn) obj;
      comparison = indexOrdinalPosition - other.indexOrdinalPosition;
    }

    if (comparison == 0)
    {
      comparison = super.compareTo(obj);
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.BaseColumn#getColumnDataType()
   */
  @Override
  public ColumnDataType getColumnDataType()
  {
    return column.getColumnDataType();
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
   * @see schemacrawler.schema.Column#getPrivileges()
   */
  @Override
  public Collection<Privilege<Column>> getPrivileges()
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

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.TypedObject#getType()
   */
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
   * @see schemacrawler.schema.Column#isAutoIncremented()
   */
  @Override
  public boolean isAutoIncremented()
  {
    return column.isAutoIncremented();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isGenerated()
   */
  @Override
  public boolean isGenerated()
  {
    return column.isGenerated();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#isHidden()
   */
  @Override
  public boolean isHidden()
  {
    return column.isHidden();
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
   * @see schemacrawler.schema.Column#isPartOfIndex()
   */
  @Override
  public boolean isPartOfIndex()
  {
    return column.isPartOfIndex();
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

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Column#lookupPrivilege(java.lang.String)
   */
  @Override
  public Optional<? extends Privilege<Column>> lookupPrivilege(final String name)
  {
    return column.lookupPrivilege(name);
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
