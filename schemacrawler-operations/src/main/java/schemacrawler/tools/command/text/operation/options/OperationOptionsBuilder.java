/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.operation.options;

import schemacrawler.schemacrawler.Query;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.text.options.BaseTextOptionsBuilder;

public final class OperationOptionsBuilder
    extends BaseTextOptionsBuilder<OperationOptionsBuilder, OperationOptions> {

  private static final String SHOW_LOBS = SCHEMACRAWLER_FORMAT_PREFIX + "data.show_lobs";
  private static final String MAX_ROWS = SCHEMACRAWLER_FORMAT_PREFIX + "data.max_rows";

  public static OperationOptionsBuilder builder() {
    return new OperationOptionsBuilder();
  }

  private String command;
  protected Operation operation;
  protected boolean isShowLobs;
  protected int maxRows;

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

    maxRows = config.getIntegerValue(MAX_ROWS, -1);
    if (maxRows < 0) {
      maxRows = Integer.MAX_VALUE;
    }

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
    maxRows = options.getMaxRows();

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

  /**
   * Show LOB data, or not.
   *
   * @param value Whether to show LOB data.
   * @return Builder
   */
  public OperationOptionsBuilder maxRows(final int value) {
    maxRows = value;
    if (maxRows < 0) {
      maxRows = Integer.MAX_VALUE;
    }
    return this;
  }

  @Override
  public Config toConfig() {
    final Config config = super.toConfig();
    config.put(SHOW_LOBS, isShowLobs);
    config.put(MAX_ROWS, maxRows);
    return config;
  }

  @Override
  public OperationOptions toOptions() {
    // Force maximum rows value for tablesample opertion
    if (OperationType.tablesample.equals(operation)) {
      maxRows = 10;
    }
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
