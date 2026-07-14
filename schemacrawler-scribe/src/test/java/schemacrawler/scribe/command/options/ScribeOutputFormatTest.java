/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import schemacrawler.scribe.okf.OkfScribeRenderer;

public class ScribeOutputFormatTest {

  @Test
  public void createsRendererForKnownFormat() {
    assertThat(
        ScribeOutputFormat.fromFormatOrNull("okf").newRenderer() instanceof OkfScribeRenderer,
        is(true));
  }

  @Test
  public void rejectsBlank() {
    assertThat(ScribeOutputFormat.isSupportedFormat("  "), is(false));
  }

  @Test
  public void rejectsNull() {
    assertThat(ScribeOutputFormat.isSupportedFormat(null), is(false));
  }

  @Test
  public void rejectsUnknownFormat() {
    assertThat(ScribeOutputFormat.isSupportedFormat("text"), is(false));
  }

  @Test
  public void supportsKnownFormatCaseInsensitively() {
    assertThat(ScribeOutputFormat.isSupportedFormat("OKF"), is(true));
  }
}
