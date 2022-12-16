/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import us.fatehi.utility.scheduler.TaskRunner;

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
    maxThreads = TaskRunner.MAX_THREADS;
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
    if (maxThreads < TaskRunner.MIN_THREADS) {
      this.maxThreads = TaskRunner.MIN_THREADS;
    } else if (maxThreads > TaskRunner.MAX_THREADS) {
      this.maxThreads = TaskRunner.MAX_THREADS;
    } else {
      this.maxThreads = maxThreads;
    }
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
