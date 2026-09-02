/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.options.ImportanceReportOutputFormat;

class ImportanceReportOutputFormatTest {

  @Test
  void supportsTextJsonAndYamlFormats() {
    assertThat(ImportanceReportOutputFormat.isSupportedFormat("text"), is(true));
    assertThat(ImportanceReportOutputFormat.isSupportedFormat("json"), is(true));
    assertThat(ImportanceReportOutputFormat.isSupportedFormat("yaml"), is(true));
  }

  @Test
  void rejectsUnknownFormats() {
    assertThrows(
        IllegalArgumentException.class, () -> ImportanceReportOutputFormat.fromFormat("xml"));
  }
}
