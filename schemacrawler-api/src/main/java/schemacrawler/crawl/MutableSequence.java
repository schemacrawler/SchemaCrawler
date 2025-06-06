/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.math.BigInteger;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;

/** Represents a database sequence. Created from metadata returned by a JDBC call. */
final class MutableSequence extends AbstractDatabaseObject implements Sequence {

  private static final long serialVersionUID = -4774695374454532899L;

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
