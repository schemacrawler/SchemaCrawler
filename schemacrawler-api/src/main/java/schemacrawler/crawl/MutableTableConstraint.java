/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableConstraintType;

/**
 * Represents a table constraint.
 */
class MutableTableConstraint
  extends AbstractDependantObject<Table>
  implements TableConstraint
{

  private static final long serialVersionUID = 1155277343302693656L;

  private final NamedObjectList<MutableTableConstraintColumn> columns = new NamedObjectList<>();
  private TableConstraintType tableConstraintType;
  private boolean deferrable;
  private boolean initiallyDeferred;
  private final StringBuilder definition;

  MutableTableConstraint(final Table parent, final String name)
  {
    super(new TableReference(parent), name);
    definition = new StringBuilder();
  }

  /**
   * {@inheritDoc}
   *
   * @see TableConstraint#getColumns()
   */
  @Override
  public List<TableConstraintColumn> getColumns()
  {
    return new ArrayList<TableConstraintColumn>(columns.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.TableConstraint#getDefinition()
   */
  @Override
  public String getDefinition()
  {
    return definition.toString();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.TableConstraint#getTableConstraintType()
   */
  @Override
  public TableConstraintType getTableConstraintType()
  {
    return tableConstraintType;
  }

  @Override
  public TableConstraintType getType()
  {
    return getTableConstraintType();
  }

  @Override
  public boolean hasDefinition()
  {
    return definition.length() > 0;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.TableConstraint#isDeferrable()
   */
  @Override
  public boolean isDeferrable()
  {
    return deferrable;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.TableConstraint#isInitiallyDeferred()
   */
  @Override
  public boolean isInitiallyDeferred()
  {
    return initiallyDeferred;
  }

  public void setTableConstraintType(final TableConstraintType tableConstraintType)
  {
    this.tableConstraintType = tableConstraintType;
  }

  void addColumn(final MutableTableConstraintColumn column)
  {
    columns.add(column);
  }

  void appendDefinition(final String definition)
  {
    if (definition != null)
    {
      this.definition.append(definition);
    }
  }

  void setDeferrable(final boolean deferrable)
  {
    this.deferrable = deferrable;
  }

  void setInitiallyDeferred(final boolean initiallyDeferred)
  {
    this.initiallyDeferred = initiallyDeferred;
  }

}
