/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schemacrawler;

import static us.fatehi.utility.scheduler.TaskRunner.MAX_THREADS;
import static us.fatehi.utility.scheduler.TaskRunner.MIN_THREADS;

public final class LoadOptionsBuilder implements OptionsBuilder<LoadOptionsBuilder, LoadOptions> {

  public static LoadOptionsBuilder builder() {
    return new LoadOptionsBuilder();
  }

  public static LoadOptions newLoadOptions() {
    return builder().toOptions();
  }

  private SchemaInfoLevel schemaInfoLevel;
  private int maxThreads;

  /** Default options. */
  private LoadOptionsBuilder() {
    schemaInfoLevel = SchemaInfoLevelBuilder.standard();
    maxThreads = MAX_THREADS;
  }

  @Override
  public LoadOptionsBuilder fromOptions(final LoadOptions options) {
    if (options == null) {
      return this;
    }

    schemaInfoLevel = options.getSchemaInfoLevel();
    maxThreads = options.getMaxThreads();

    return this;
  }

  @Override
  public LoadOptions toOptions() {
    return new LoadOptions(schemaInfoLevel, maxThreads);
  }

  public LoadOptionsBuilder withInfoLevel(final InfoLevel infoLevel) {
    if (infoLevel != null) {
      this.schemaInfoLevel = infoLevel.toSchemaInfoLevel();
    }
    return this;
  }

  /**
   * IMPORTANT: Multi-threading is not implemented. It is possibly future functionality.
   *
   * @param maxThreads Maximum number of threads for multi-threaded operation.
   * @return Maximum number of threads.
   */
  public LoadOptionsBuilder withMaxThreads(final int maxThreads) {
    this.maxThreads = Math.min(Math.max(maxThreads, MIN_THREADS), MAX_THREADS);
    return this;
  }

  public LoadOptionsBuilder withSchemaInfoLevel(final SchemaInfoLevel schemaInfoLevel) {
    if (schemaInfoLevel != null) {
      this.schemaInfoLevel = schemaInfoLevel;
    }
    return this;
  }

  public LoadOptionsBuilder withSchemaInfoLevelBuilder(
      final SchemaInfoLevelBuilder schemaInfoLevelBuilder) {
    if (schemaInfoLevelBuilder != null) {
      this.schemaInfoLevel = schemaInfoLevelBuilder.toOptions();
    }
    return this;
  }
}
