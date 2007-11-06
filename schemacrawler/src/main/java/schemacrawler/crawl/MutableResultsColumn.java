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

  private boolean isAutoIncrement;
  private boolean isCaseSensitive;
  private boolean isCurrency;
  private boolean isDefinitelyWritable;
  private boolean isReadOnly;
  private boolean isSearchable;
  private boolean isSigned;
  private boolean isWritable;

  MutableResultsColumn(final String name, final NamedObject parent)
  {
    super(name, parent);
  }

  public boolean isAutoIncrement()
  {
    return isAutoIncrement;
  }

  public boolean isCaseSensitive()
  {
    return isCaseSensitive;
  }

  public boolean isCurrency()
  {
    return isCurrency;
  }

  public boolean isDefinitelyWritable()
  {
    return isDefinitelyWritable;
  }

  public boolean isReadOnly()
  {
    return isReadOnly;
  }

  public boolean isSearchable()
  {
    return isSearchable;
  }

  public boolean isSigned()
  {
    return isSigned;
  }

  public boolean isWritable()
  {
    return isWritable;
  }

  void setAutoIncrement(final boolean isAutoIncrement)
  {
    this.isAutoIncrement = isAutoIncrement;
  }

  void setCaseSensitive(final boolean isCaseSensitive)
  {
    this.isCaseSensitive = isCaseSensitive;
  }

  void setCurrency(final boolean isCurrency)
  {
    this.isCurrency = isCurrency;
  }

  void setDefinitelyWritable(final boolean isDefinitelyWritable)
  {
    this.isDefinitelyWritable = isDefinitelyWritable;
  }

  void setReadOnly(final boolean isReadOnly)
  {
    this.isReadOnly = isReadOnly;
  }

  void setSearchable(final boolean isSearchable)
  {
    this.isSearchable = isSearchable;
  }

  void setSigned(final boolean isSigned)
  {
    this.isSigned = isSigned;
  }

  void setWritable(final boolean isWritable)
  {
    this.isWritable = isWritable;
  }

}
