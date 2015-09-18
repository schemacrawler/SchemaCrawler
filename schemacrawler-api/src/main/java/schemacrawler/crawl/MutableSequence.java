/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import java.math.BigInteger;

import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;

/**
 * Represents a database sequence. Created from metadata returned by a JDBC
 * call.
 *
 * @author Sualeh Fatehi
 */
final class MutableSequence
  extends AbstractDatabaseObject
  implements Sequence
{

  private static final long serialVersionUID = -4774695374454532899L;

  private BigInteger minimumValue;
  private BigInteger maximumValue;
  private long increment;
  private boolean cycle;

  MutableSequence(final Schema schema, final String name)
  {
    super(schema, name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Sequence#getIncrement()
   */
  @Override
  public long getIncrement()
  {
    return increment;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Sequence#getMaximumValue()
   */
  @Override
  public BigInteger getMaximumValue()
  {
    return maximumValue;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Sequence#getMinimumValue()
   */
  @Override
  public BigInteger getMinimumValue()
  {
    return minimumValue;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.Sequence#isCycle()
   */
  @Override
  public boolean isCycle()
  {
    return cycle;
  }

  void setCycle(final boolean cycle)
  {
    this.cycle = cycle;
  }

  void setIncrement(final long increment)
  {
    this.increment = increment;
  }

  void setMaximumValue(final BigInteger maximumValue)
  {
    this.maximumValue = maximumValue;
  }

  void setMinimumValue(final BigInteger minimumValue)
  {
    this.minimumValue = minimumValue;
  }

}
