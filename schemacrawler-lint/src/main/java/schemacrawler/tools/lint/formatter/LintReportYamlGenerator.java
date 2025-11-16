/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint.formatter;

import schemacrawler.tools.options.OutputOptions;
import tools.jackson.dataformat.yaml.YAMLMapper;

public final class LintReportYamlGenerator extends BaseLintReportJacksonGenerator {

  public LintReportYamlGenerator(final OutputOptions outputOptions) {
    super(outputOptions);
  }

  @Override
  protected final YAMLMapper.Builder newMapperBuilder() {
    return YAMLMapper.builder();
  }
}
