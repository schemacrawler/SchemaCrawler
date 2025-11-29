/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint.formatter;

import schemacrawler.tools.options.OutputOptions;
import tools.jackson.databind.json.JsonMapper;

public final class LintReportJsonGenerator extends BaseLintReportJacksonGenerator {

  public LintReportJsonGenerator(final OutputOptions outputOptions) {
    super(outputOptions);
  }

  @Override
  protected final JsonMapper.Builder newMapperBuilder() {
    return JsonMapper.builder();
  }
}
