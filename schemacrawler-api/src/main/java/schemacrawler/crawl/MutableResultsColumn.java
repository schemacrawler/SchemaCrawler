/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.Table;

/**
 * Represents a column in a result set.
 *
 * @author Sualeh Fatehi
 */
final class MutableResultsColumn
  extends AbstractColumn<Table>
  implements ResultsColumn
{

  private static final long serialVersionUID = -6983013302549352559L;
  private boolean autoIncrement;
  private boolean caseSensitive;
  private boolean currency;
  private boolean definitelyWritable;
  private int displaySize;
  private String label;
  private boolean readOnly;
  private boolean searchable;
  private boolean signed;
  private boolean writable;

  MutableResultsColumn(final Table parent, final String name)
  {
    super(new TableReference(parent), name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getDisplaySize()
  {
    return displaySize;
  }

  void setDisplaySize(final int displaySize)
  {
    this.displaySize = displaySize;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLabel()
  {
    return label;
  }

  void setLabel(final String label)
  {
    this.label = label;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAutoIncrement()
  {
    return autoIncrement;
  }

  void setAutoIncrement(final boolean isAutoIncrement)
  {
    autoIncrement = isAutoIncrement;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCaseSensitive()
  {
    return caseSensitive;
  }

  void setCaseSensitive(final boolean isCaseSensitive)
  {
    caseSensitive = isCaseSensitive;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCurrency()
  {
    return currency;
  }

  void setCurrency(final boolean isCurrency)
  {
    currency = isCurrency;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDefinitelyWritable()
  {
    return definitelyWritable;
  }

  void setDefinitelyWritable(final boolean isDefinitelyWritable)
  {
    definitelyWritable = isDefinitelyWritable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isReadOnly()
  {
    return readOnly;
  }

  void setReadOnly(final boolean isReadOnly)
  {
    readOnly = isReadOnly;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSearchable()
  {
    return searchable;
  }

  void setSearchable(final boolean isSearchable)
  {
    searchable = isSearchable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSigned()
  {
    return signed;
  }

  void setSigned(final boolean isSigned)
  {
    signed = isSigned;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isWritable()
  {
    return writable;
  }

  void setWritable(final boolean isWritable)
  {
    writable = isWritable;
  }

  @Override
  public final List<String> toUniqueLookupKey()
  {
    // Make a defensive copy
    final List<String> lookupKey = new ArrayList<>(super.toUniqueLookupKey());
    lookupKey.add(label);
    return lookupKey;
  }

}
