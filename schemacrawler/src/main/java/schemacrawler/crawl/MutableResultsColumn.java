/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

  MutableResultsColumn(final String name, final NamedObject parent)
  {
    super(name, parent);
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
