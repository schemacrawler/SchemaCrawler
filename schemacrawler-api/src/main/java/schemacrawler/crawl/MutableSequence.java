/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.io.Serial;
import java.math.BigInteger;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;

/** Represents a database sequence. Created from metadata returned by a JDBC call. */
final class MutableSequence extends AbstractDatabaseObject implements Sequence {

  @Serial private static final long serialVersionUID = -4774695374454532899L;

  private boolean cycle;
  private long increment;
  private BigInteger startValue;
  private BigInteger maximumValue;
  private BigInteger minimumValue;

  MutableSequence(final Schema schema, final String name) {
    super(schema, name);
  }

  /** {@inheritDoc} */
  @Override
  public long getIncrement() {
    return increment;
  }

  /** {@inheritDoc} */
  @Override
  public BigInteger getMaximumValue() {
    return maximumValue;
  }

  /** {@inheritDoc} */
  @Override
  public BigInteger getMinimumValue() {
    return minimumValue;
  }

  @Override
  public BigInteger getStartValue() {
    return startValue;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isCycle() {
    return cycle;
  }

  void setCycle(final boolean cycle) {
    this.cycle = cycle;
  }

  void setIncrement(final long increment) {
    this.increment = increment;
  }

  void setMaximumValue(final BigInteger maximumValue) {
    this.maximumValue = maximumValue;
  }

  void setMinimumValue(final BigInteger minimumValue) {
    this.minimumValue = minimumValue;
  }

  void setStartValue(final BigInteger startValue) {
    this.startValue = startValue;
  }
}
