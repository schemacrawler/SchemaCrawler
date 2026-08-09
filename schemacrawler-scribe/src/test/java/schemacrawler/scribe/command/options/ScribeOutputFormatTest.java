/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import schemacrawler.scribe.okf.OpenKnowledgeFormatRenderer;

public class ScribeOutputFormatTest {

  @Test
  public void allValuesNotNull() {
    assertThat(ScribeOutputFormat.values(), is(notNullValue()));
  }

  @Test
  public void acceptsBlank() {
    assertThat(ScribeOutputFormat.isSupportedFormat("  "), is(true));
  }

  @Test
  public void acceptsNull() {
    assertThat(ScribeOutputFormat.isSupportedFormat(null), is(true));
  }

  @Test
  public void createsRendererForKnownFormat() {
    assertThat(
        ScribeOutputFormat.fromFormatOrNull("okf").newRenderer()
            instanceof OpenKnowledgeFormatRenderer,
        is(true));
  }

  @Test
  public void rejectsUnknownFormat() {
    assertThat(ScribeOutputFormat.isSupportedFormat("unsupported"), is(false));
  }

  @Test
  public void getDescription() {
    for (final ScribeOutputFormat format : ScribeOutputFormat.values()) {
      assertThat(format.getDescription(), is(not(blankOrNullString())));
    }
  }

  @Test
  public void getFormat() {
    for (final ScribeOutputFormat format : ScribeOutputFormat.values()) {
      assertThat(format.getFormat(), is(not(blankOrNullString())));
    }
  }

  @Test
  public void getFormats() {
    for (final ScribeOutputFormat format : ScribeOutputFormat.values()) {
      assertThat(format.getFormats(), is(not(empty())));
    }
  }

  @Test
  public void supportsKnownFormatCaseInsensitively() {
    assertThat(ScribeOutputFormat.isSupportedFormat("OKF"), is(true));
  }

  @Test
  public void toStringNotBlank() {
    for (final ScribeOutputFormat format : ScribeOutputFormat.values()) {
      assertThat(format.toString(), is(not(blankOrNullString())));
    }
  }

  @Test
  public void supportsText() {
    assertThat(ScribeOutputFormat.isSupportedFormat("text"), is(true));
  }
}
