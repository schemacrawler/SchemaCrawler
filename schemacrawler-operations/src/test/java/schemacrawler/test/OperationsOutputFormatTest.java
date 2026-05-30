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

import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.text.operation.options.OperationsOutputFormat;

public class OperationsOutputFormatTest {

  @Test
  public void allValuesNotNull() {
    assertThat(OperationsOutputFormat.values(), is(notNullValue()));
  }

  @Test
  public void fromFormatBlank() {
    assertThat(OperationsOutputFormat.fromFormat(""), is(OperationsOutputFormat.json));
  }

  @Test
  public void fromFormatHtml() {
    assertThat(OperationsOutputFormat.fromFormat("html"), is(OperationsOutputFormat.html));
  }

  @Test
  public void fromFormatJson() {
    assertThat(OperationsOutputFormat.fromFormat("json"), is(OperationsOutputFormat.json));
  }

  @Test
  public void fromFormatNull() {
    assertThat(OperationsOutputFormat.fromFormat(null), is(OperationsOutputFormat.json));
  }

  @Test
  public void fromFormatText() {
    assertThat(OperationsOutputFormat.fromFormat("text"), is(OperationsOutputFormat.text));
  }

  @Test
  public void fromFormatTxt() {
    assertThat(OperationsOutputFormat.fromFormat("txt"), is(OperationsOutputFormat.text));
  }

  @Test
  public void fromFormatUnknown() {
    assertThat(
        OperationsOutputFormat.fromFormat("unknown_format"), is(OperationsOutputFormat.json));
  }

  @Test
  public void getDescription() {
    for (final OperationsOutputFormat format : OperationsOutputFormat.values()) {
      assertThat(format.getDescription(), is(not(blankOrNullString())));
    }
  }

  @Test
  public void getFormat() {
    for (final OperationsOutputFormat format : OperationsOutputFormat.values()) {
      assertThat(format.getFormat(), is(not(blankOrNullString())));
    }
  }

  @Test
  public void getFormats() {
    for (final OperationsOutputFormat format : OperationsOutputFormat.values()) {
      assertThat(format.getFormats(), is(not(empty())));
    }
  }

  @Test
  public void isSupportedFormatBlank() {
    assertThat(OperationsOutputFormat.isSupportedFormat(""), is(false));
  }

  @Test
  public void isSupportedFormatNull() {
    assertThat(OperationsOutputFormat.isSupportedFormat(null), is(false));
  }

  @Test
  public void isSupportedFormatText() {
    assertThat(OperationsOutputFormat.isSupportedFormat("text"), is(true));
  }

  @Test
  public void isSupportedFormatUnknown() {
    assertThat(OperationsOutputFormat.isSupportedFormat("unknown"), is(false));
  }

  @Test
  public void toStringNotBlank() {
    for (final OperationsOutputFormat format : OperationsOutputFormat.values()) {
      assertThat(format.toString(), is(not(blankOrNullString())));
    }
  }
}
