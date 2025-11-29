/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.operation;

import schemacrawler.tools.command.text.operation.options.Operation;
import schemacrawler.tools.command.text.operation.options.OperationOptions;
import schemacrawler.tools.options.OutputOptions;
import tools.jackson.databind.json.JsonMapper;

/** JSON formatting of data. */
public final class DataJsonFormatter extends BaseJacksonFormatter {

  public DataJsonFormatter(
      Operation operation, OperationOptions options, OutputOptions outputOptions) {
    super(operation, options, outputOptions);
  }

  @Override
  protected final JsonMapper.Builder newMapperBuilder() {
    return JsonMapper.builder();
  }
}
