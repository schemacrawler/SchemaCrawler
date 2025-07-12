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

  protected OperationOptions(final OperationOptionsBuilder builder) {
    super(builder);

    operation = requireNonNull(builder.operation, "No operation provided");
    isShowLobs = builder.isShowLobs;
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
