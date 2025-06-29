/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.operation.options;

import static java.util.Objects.requireNonNull;
import schemacrawler.schemacrawler.Query;
import schemacrawler.tools.text.options.BaseTextOptions;

public final class OperationOptions extends BaseTextOptions {

  private final Operation operation;
  private final boolean isShowLobs;
  protected final int maxRows;

  protected OperationOptions(final OperationOptionsBuilder builder) {
    super(builder);

    operation = requireNonNull(builder.operation, "No operation provided");
    isShowLobs = builder.isShowLobs;
    maxRows = builder.maxRows;
    if (maxRows < 0) {
      throw new IllegalArgumentException("Max rows cannot be negative");
    }
  }

  /**
   * Gets the maximum number of data rows to display.
   *
   * @return Maximum number of rows
   */
  public int getMaxRows() {
    return maxRows;
  }

  public Operation getOperation() {
    return operation;
  }

  public Query getQuery() {
    return operation.getQuery();
  }

  /**
   * Whether to show LOBs.
   *
   * @return Whether to show LOBs.
   */
  public boolean isShowLobs() {
    return isShowLobs;
  }
}
