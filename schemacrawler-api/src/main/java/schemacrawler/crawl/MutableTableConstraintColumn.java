package schemacrawler.crawl;


import java.util.Collection;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;

final class MutableTableConstraintColumn
  extends AbstractDependantObject<Table>
  implements TableConstraintColumn
{

  private static final long serialVersionUID = -6923211341742623556L;

  private final Column column;
  private final TableConstraint tableConstraint;
  private int tableConstraintOrdinalPosition;

  MutableTableConstraintColumn(final TableConstraint tableConstraint,
                               final MutableColumn column)
  {
    super(column.getParent(), column.getName());
    this.tableConstraint = tableConstraint;
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

    if (obj instanceof MutableTableConstraintColumn)
    {
      final MutableTableConstraintColumn other = (MutableTableConstraintColumn) obj;
      comparison = tableConstraintOrdinalPosition
                   - other.tableConstraintOrdinalPosition;
    }

    if (comparison == 0)
    {
      comparison = super.compareTo(obj);
    }

    return comparison;
  }

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
   * @see schemacrawler.schema.TableConstraintColumn#getTableConstraint()
   */
  @Override
  public TableConstraint getTableConstraint()
  {
    return tableConstraint;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.TableConstraintColumn#getTableConstraintOrdinalPosition()
   */
  @Override
  public int getTableConstraintOrdinalPosition()
  {
    return tableConstraintOrdinalPosition;
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
   * @see schemacrawler.schema.Column#isPartOfUniqueIndex()()
   */
  @Override
  public boolean isPartOfUniqueIndex()
  {
    return column.isPartOfUniqueIndex();
  }

  void setTableConstraintOrdinalPosition(final int indexOrdinalPosition)
  {
    tableConstraintOrdinalPosition = indexOrdinalPosition;
  }

}
