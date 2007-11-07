/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


import schemacrawler.schema.NamedObject;
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

  private boolean autoIncrement;
  private boolean caseSensitive;
  private boolean currency;
  private boolean definitelyWritable;
  private boolean readOnly;
  private boolean searchable;
  private boolean signed;
  private boolean writable;

  MutableResultsColumn(final String name, final NamedObject parent)
  {
    super(name, parent);
  }

  public boolean isAutoIncrement()
  {
    return autoIncrement;
  }

  public boolean isCaseSensitive()
  {
    return caseSensitive;
  }

  public boolean isCurrency()
  {
    return currency;
  }

  public boolean isDefinitelyWritable()
  {
    return definitelyWritable;
  }

  public boolean isReadOnly()
  {
    return readOnly;
  }

  public boolean isSearchable()
  {
    return searchable;
  }

  public boolean isSigned()
  {
    return signed;
  }

  public boolean isWritable()
  {
    return writable;
  }

  void setAutoIncrement(final boolean isAutoIncrement)
  {
    this.autoIncrement = isAutoIncrement;
  }

  void setCaseSensitive(final boolean isCaseSensitive)
  {
    this.caseSensitive = isCaseSensitive;
  }

  void setCurrency(final boolean isCurrency)
  {
    this.currency = isCurrency;
  }

  void setDefinitelyWritable(final boolean isDefinitelyWritable)
  {
    this.definitelyWritable = isDefinitelyWritable;
  }

  void setReadOnly(final boolean isReadOnly)
  {
    this.readOnly = isReadOnly;
  }

  void setSearchable(final boolean isSearchable)
  {
    this.searchable = isSearchable;
  }

  void setSigned(final boolean isSigned)
  {
    this.signed = isSigned;
  }

  void setWritable(final boolean isWritable)
  {
    this.writable = isWritable;
  }

}
