/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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
