/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
