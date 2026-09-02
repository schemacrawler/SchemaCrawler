/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.options.ImportanceReportOutputFormat;

class ImportanceReportOutputFormatTest {

  @Test
  void supportsTextJsonAndYamlFormats() {
    assertThat(ImportanceReportOutputFormat.isSupportedFormat("text"), is(true));
    assertThat(ImportanceReportOutputFormat.isSupportedFormat("txt"), is(true));
    assertThat(ImportanceReportOutputFormat.isSupportedFormat("json"), is(true));
    assertThat(ImportanceReportOutputFormat.isSupportedFormat("yaml"), is(true));
  }

  @Test
  void resolvesSupportedFormatsIgnoringCase() {
    assertThat(
        ImportanceReportOutputFormat.fromFormat("TEXT"), is(ImportanceReportOutputFormat.text));
    assertThat(
        ImportanceReportOutputFormat.fromFormat("txt"), is(ImportanceReportOutputFormat.text));
    assertThat(
        ImportanceReportOutputFormat.fromFormat("JSON"), is(ImportanceReportOutputFormat.json));
    assertThat(
        ImportanceReportOutputFormat.fromFormat("YAML"), is(ImportanceReportOutputFormat.yaml));
  }

  @Test
  void exposesFormatMetadata() {
    for (final ImportanceReportOutputFormat format : ImportanceReportOutputFormat.values()) {
      assertThat(format.getDescription(), is(not(blankOrNullString())));
      assertThat(format.getFormat(), is(not(blankOrNullString())));
      assertThat(format.getFormats(), is(not(empty())));
      assertThat(format.toString(), is(not(blankOrNullString())));
    }
  }

  @Test
  void rejectsUnsupportedFormats() {
    assertThat(ImportanceReportOutputFormat.isSupportedFormat(null), is(false));
    assertThat(ImportanceReportOutputFormat.isSupportedFormat("xml"), is(false));
    assertThrows(
        IllegalArgumentException.class, () -> ImportanceReportOutputFormat.fromFormat("xml"));
    assertThrows(
        IllegalArgumentException.class, () -> ImportanceReportOutputFormat.fromFormat(null));
  }

  @Test
  void providesAllOutputFormats() {
    assertThat(ImportanceReportOutputFormat.values(), is(notNullValue()));
  }
}
