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

package schemacrawler.tools.command.text.operation.options;

import schemacrawler.schemacrawler.Query;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.text.options.BaseTextOptionsBuilder;

public final class OperationOptionsBuilder
    extends BaseTextOptionsBuilder<OperationOptionsBuilder, OperationOptions> {
  private static final String SHOW_LOBS = SCHEMACRAWLER_FORMAT_PREFIX + "data.show_lobs";

  public static OperationOptionsBuilder builder() {
    return new OperationOptionsBuilder();
  }

  private String command;
  protected Operation operation;
  protected boolean isShowLobs;

  private OperationOptionsBuilder() {
    // Set default values, if any
  }

  @Override
  public OperationOptionsBuilder fromConfig(final Config config) {
    if (config == null) {
      return this;
    }
    super.fromConfig(config);

    isShowLobs = config.getBooleanValue(SHOW_LOBS, false);
    operation = getQueryFromCommand(config);

    return this;
  }

  @Override
  public OperationOptionsBuilder fromOptions(final OperationOptions options) {
    if (options == null) {
      return this;
    }
    super.fromOptions(options);

    isShowLobs = options.isShowLobs();

    return this;
  }

  public OperationOptionsBuilder showLobs() {
    return showLobs(true);
  }

  /**
   * Show LOB data, or not.
   *
   * @param value Whether to show LOB data.
   * @return Builder
   */
  public OperationOptionsBuilder showLobs(final boolean value) {
    isShowLobs = value;
    return this;
  }

  @Override
  public Config toConfig() {
    final Config config = super.toConfig();
    config.put(SHOW_LOBS, isShowLobs);
    return config;
  }

  @Override
  public OperationOptions toOptions() {
    return new OperationOptions(this);
  }

  public OperationOptionsBuilder withCommand(final String command) {
    this.command = command;
    operation = getOperationFromCommand();
    return this;
  }

  private Operation getOperationFromCommand() {
    Operation operation = null;
    try {
      operation = OperationType.valueOf(command);
    } catch (final IllegalArgumentException | NullPointerException e) {
      operation = this.operation;
    }
    return operation;
  }

  private Operation getQueryFromCommand(final Config config) {
    final Operation operation;
    if (config.containsKey(command)) {
      final String queryName = command;
      final String queryString = config.getStringValue(queryName, null);
      operation = new QueryOperation(new Query(queryName, queryString));
    } else {
      operation = this.operation;
    }

    return operation;
  }
}
