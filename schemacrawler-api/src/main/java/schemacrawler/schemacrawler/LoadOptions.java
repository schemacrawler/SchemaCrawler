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

package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;
import us.fatehi.utility.ObjectToString;

public final class LoadOptions implements Options {

  private final SchemaInfoLevel schemaInfoLevel;
  private final int maxThreads;

  LoadOptions(final SchemaInfoLevel schemaInfoLevel, final int maxThreads) {
    this.schemaInfoLevel = requireNonNull(schemaInfoLevel, "No schema info level provided");
    this.maxThreads = maxThreads;
  }

  /**
   * Maximum number of threads.
   *
   * @return Maximum number of threads.
   */
  public int getMaxThreads() {
    return maxThreads;
  }

  /**
   * Gets the schema information level, identifying to what level the schema should be crawled.
   *
   * @return Schema information level.
   */
  public SchemaInfoLevel getSchemaInfoLevel() {
    return schemaInfoLevel;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
