/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

import java.math.BigInteger;

/** Represents a database sequence. */
public interface Sequence extends DatabaseObject {

  /**
   * Gets the increment of the sequence.
   *
   * @return Increment of the sequence.
   */
  long getIncrement();

  /**
   * Gets the maximum value of the sequence.
   *
   * @return Maximum value of the sequence.
   */
  BigInteger getMaximumValue();

  /**
   * Gets the minimum value of the sequence.
   *
   * @return Minimum value of the sequence.
   */
  BigInteger getMinimumValue();

  /**
   * Gets the start value of the sequence.
   *
   * @return Start value of the sequence.
   */
  BigInteger getStartValue();

  /**
   * Indicates whether or not the sequence can continue to generate values after reaching its
   * maximum or minimum value. return True if the sequence continues to generate values.
   */
  boolean isCycle();
}
