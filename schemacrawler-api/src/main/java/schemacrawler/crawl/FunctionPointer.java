/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import schemacrawler.schema.Function;

final class FunctionPointer extends DatabaseObjectReference<Function> {

  @Serial private static final long serialVersionUID = -5166020646865781875L;

  FunctionPointer(final Function function) {
    super(requireNonNull(function, "No function provided"), new FunctionPartial(function));
  }
}
