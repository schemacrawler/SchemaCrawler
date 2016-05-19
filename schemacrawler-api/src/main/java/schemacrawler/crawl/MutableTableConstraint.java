/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Constraint;
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
   * @see Constraint#getColumns()
   */
  @Override
  public List<TableConstraintColumn> getColumns()
  {
    return new ArrayList<TableConstraintColumn>(columns.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Constraint#getConstraintType()
   */
  @Override
  public TableConstraintType getConstraintType()
  {
    return tableConstraintType;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Constraint#getDefinition()
   */
  @Override
  public String getDefinition()
  {
    return definition.toString();
  }

  @Override
  public TableConstraintType getType()
  {
    return getConstraintType();
  }

  @Override
  public boolean hasDefinition()
  {
    return definition.length() > 0;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Constraint#isDeferrable()
   */
  @Override
  public boolean isDeferrable()
  {
    return deferrable;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Constraint#isInitiallyDeferred()
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
