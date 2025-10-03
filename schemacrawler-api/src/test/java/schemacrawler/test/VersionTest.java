/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static java.util.regex.Pattern.DOTALL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesRegex;
import static schemacrawler.test.utility.FileHasContent.contentsOf;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import schemacrawler.Version;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;

@CaptureSystemStreams
public class VersionTest {

  @Test
  public void version(final CapturedSystemStreams streams) throws Exception {
    final Pattern VERSION = Pattern.compile("SchemaCrawler 16\\.2[89]\\.\\d{1,2}\\R.*", DOTALL);

    Version.main(new String[0]);

    assertThat(contentsOf(streams.out()), matchesRegex(VERSION));
    assertThat(outputOf(streams.err()), hasNoContent());
  }
}
