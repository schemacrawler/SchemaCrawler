/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableConstraintType;

/** Represents a table constraint. */
class MutableTableConstraint extends AbstractDependantObject<Table> implements TableConstraint {

  private static final long serialVersionUID = 1155277343302693656L;

  private final NamedObjectList<MutableTableConstraintColumn> columns = new NamedObjectList<>();
  private final StringBuffer definition;
  private boolean deferrable;
  private boolean initiallyDeferred;

  private TableConstraintType tableConstraintType;

  MutableTableConstraint(final Table parent, final String name) {
    super(new TablePointer(parent), name);
    definition = new StringBuffer();
  }

  /** {@inheritDoc} */
  @Override
  public List<TableConstraintColumn> getConstrainedColumns() {
    return new ArrayList<>(columns.values());
  }

  /** {@inheritDoc} */
  @Override
  public String getDefinition() {
    return definition.toString();
  }

  @Override
  public TableConstraintType getType() {
    if (tableConstraintType != null) {
      return tableConstraintType;
    } else {
      return TableConstraintType.unknown;
    }
  }

  @Override
  public boolean hasDefinition() {
    return definition.length() > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isDeferrable() {
    return deferrable;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isInitiallyDeferred() {
    return initiallyDeferred;
  }

  void addColumn(final MutableTableConstraintColumn column) {
    columns.add(column);
  }

  void appendDefinition(final String definition) {
    if (definition != null) {
      this.definition.append(definition);
    }
  }

  void setDeferrable(final boolean deferrable) {
    this.deferrable = deferrable;
  }

  void setInitiallyDeferred(final boolean initiallyDeferred) {
    this.initiallyDeferred = initiallyDeferred;
  }

  void setTableConstraintType(final TableConstraintType tableConstraintType) {
    this.tableConstraintType = tableConstraintType;
  }
}
