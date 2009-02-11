/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ResultsColumn;

/**
 * Represents a column in a result set.
 * 
 * @author Sualeh Fatehi
 */
final class MutableResultsColumn
  extends AbstractColumn
  implements ResultsColumn
{

  private static final long serialVersionUID = -6983013302549352559L;

  private String label;
  private int displaySize;
  private boolean autoIncrement;
  private boolean caseSensitive;
  private boolean currency;
  private boolean definitelyWritable;
  private boolean readOnly;
  private boolean searchable;
  private boolean signed;
  private boolean writable;

  MutableResultsColumn(final String name, final DatabaseObject parent)
  {
    super(parent, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ResultsColumn#getDisplaySize()
   */
  public int getDisplaySize()
  {
    return displaySize;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ResultsColumn#getLabel()
   */
  public String getLabel()
  {
    return label;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ResultsColumn#isAutoIncrement()
   */
  public boolean isAutoIncrement()
  {
    return autoIncrement;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ResultsColumn#isCaseSensitive()
   */
  public boolean isCaseSensitive()
  {
    return caseSensitive;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ResultsColumn#isCurrency()
   */
  public boolean isCurrency()
  {
    return currency;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ResultsColumn#isDefinitelyWritable()
   */
  public boolean isDefinitelyWritable()
  {
    return definitelyWritable;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ResultsColumn#isReadOnly()
   */
  public boolean isReadOnly()
  {
    return readOnly;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ResultsColumn#isSearchable()
   */
  public boolean isSearchable()
  {
    return searchable;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ResultsColumn#isSigned()
   */
  public boolean isSigned()
  {
    return signed;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ResultsColumn#isWritable()
   */
  public boolean isWritable()
  {
    return writable;
  }

  void setAutoIncrement(final boolean isAutoIncrement)
  {
    autoIncrement = isAutoIncrement;
  }

  void setCaseSensitive(final boolean isCaseSensitive)
  {
    caseSensitive = isCaseSensitive;
  }

  void setCurrency(final boolean isCurrency)
  {
    currency = isCurrency;
  }

  void setDefinitelyWritable(final boolean isDefinitelyWritable)
  {
    definitelyWritable = isDefinitelyWritable;
  }

  void setDisplaySize(final int displaySize)
  {
    this.displaySize = displaySize;
  }

  void setLabel(final String label)
  {
    this.label = label;
  }

  void setReadOnly(final boolean isReadOnly)
  {
    readOnly = isReadOnly;
  }

  void setSearchable(final boolean isSearchable)
  {
    searchable = isSearchable;
  }

  void setSigned(final boolean isSigned)
  {
    signed = isSigned;
  }

  void setWritable(final boolean isWritable)
  {
    writable = isWritable;
  }

}
