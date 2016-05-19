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


import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintType;

/**
 * Represents a primary key in a table.
 *
 * @author Sualeh Fatehi
 */
final class MutablePrimaryKey
  extends MutableIndex
  implements PrimaryKey
{

  private static final long serialVersionUID = -7169206178562782087L;

  private Boolean deferrable; // initialize to null
  private Boolean initiallyDeferred; // initialize to null

  /**
   * Copies information from an index.
   *
   * @param index
   *        Index
   */
  MutablePrimaryKey(final MutableIndex index)
  {
    super(index.getParent(), index.getName());
    setCardinality(index.getCardinality());
    setPages(index.getPages());
    setRemarks(index.getRemarks());
    setIndexType(index.getIndexType());
    setUnique(index.isUnique());
    // Copy columns
    for (final IndexColumn column: index)
    {
      addColumn((MutableIndexColumn) column);
    }
  }

  MutablePrimaryKey(final Table parent, final String name)
  {
    super(parent, name);
  }

  @Override
  public TableConstraintType getConstraintType()
  {
    return TableConstraintType.primary_key;
  }

  @Override
  public boolean isDeferrable()
  {
    if (deferrable == null)
    {
      throw new NotLoadedException(this);
    }
    return deferrable;
  }

  @Override
  public boolean isInitiallyDeferred()
  {
    if (initiallyDeferred == null)
    {
      throw new NotLoadedException(this);
    }
    return initiallyDeferred;
  }

  /**
   * {@inheritDoc}
   *
   * @see Index#isUnique()
   */
  @Override
  public final boolean isUnique()
  {
    return true;
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
