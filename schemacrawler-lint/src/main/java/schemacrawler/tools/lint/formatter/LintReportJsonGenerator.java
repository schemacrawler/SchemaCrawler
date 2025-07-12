/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;

import schemacrawler.tools.options.OutputOptions;

public class LintReportJsonGenerator extends BaseLintReportJacksonGenerator {

  public LintReportJsonGenerator(final OutputOptions outputOptions) {
    super(outputOptions);
  }

  @Override
  protected ObjectMapper newObjectMapper() {
    return new ObjectMapper();
  }
}
