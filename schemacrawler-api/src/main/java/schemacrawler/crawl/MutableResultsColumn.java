/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.io.Serial;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.Table;

/** Represents a column in a result set. */
final class MutableResultsColumn extends AbstractColumn<Table> implements ResultsColumn {

  @Serial private static final long serialVersionUID = -6983013302549352559L;

  private transient NamedObjectKey key;
  private boolean autoIncrement;
  private boolean caseSensitive;
  private boolean currency;
  private boolean definitelyWritable;
  private int displaySize;
  private final String label;
  private boolean readOnly;
  private boolean searchable;
  private boolean signed;
  private boolean writable;

  MutableResultsColumn(final Table parent, final String name, final String label) {
    super(new TablePointer(parent), name);
    this.label = label;
  }

  /** {@inheritDoc} */
  @Override
  public int getDisplaySize() {
    return displaySize;
  }

  /** {@inheritDoc} */
  @Override
  public String getLabel() {
    return label;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAutoIncrement() {
    return autoIncrement;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isCurrency() {
    return currency;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isDefinitelyWritable() {
    return definitelyWritable;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isReadOnly() {
    return readOnly;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSearchable() {
    return searchable;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSigned() {
    return signed;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isWritable() {
    return writable;
  }

  @Override
  public NamedObjectKey key() {
    buildKey();
    return key;
  }

  void setAutoIncrement(final boolean isAutoIncrement) {
    autoIncrement = isAutoIncrement;
  }

  void setCaseSensitive(final boolean isCaseSensitive) {
    caseSensitive = isCaseSensitive;
  }

  void setCurrency(final boolean isCurrency) {
    currency = isCurrency;
  }

  void setDefinitelyWritable(final boolean isDefinitelyWritable) {
    definitelyWritable = isDefinitelyWritable;
  }

  void setDisplaySize(final int displaySize) {
    this.displaySize = displaySize;
  }

  void setReadOnly(final boolean isReadOnly) {
    readOnly = isReadOnly;
  }

  void setSearchable(final boolean isSearchable) {
    searchable = isSearchable;
  }

  void setSigned(final boolean isSigned) {
    signed = isSigned;
  }

  void setWritable(final boolean isWritable) {
    writable = isWritable;
  }

  private void buildKey() {
    if (key != null) {
      return;
    }
    key = super.key().with(label);
  }
}
