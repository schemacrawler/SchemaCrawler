/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import com.example.ApiExample;
import com.example.ExecutableExample;
import com.example.ResultSetExample;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;

@CaptureSystemStreams
public class ExampleTest {

  @Test
  public void apiExample(final CapturedSystemStreams streams) throws Exception {
    ApiExample.main(new String[0]);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(outputOf(streams.out()), hasSameContentAs(classpathResource("ApiExample.txt")));
  }

  @Test
  public void executableExample(final CapturedSystemStreams streams) throws Exception {
    // Test
    final Path tempFile = Files.createTempFile("sc", ".out").toAbsolutePath();
    ExecutableExample.main(new String[] {tempFile.toString()});

    assertThat(outputOf(streams.err()), hasNoContent());

    final String expectedResource = "ExecutableExample.html";
    assertThat(
        outputOf(tempFile),
        hasSameContentAndTypeAs(classpathResource(expectedResource), TextOutputFormat.html));
  }

  @Test
  public void resultSetExample(final CapturedSystemStreams streams) throws Exception {
    ResultSetExample.main(new String[0]);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        outputOf(streams.out()), hasSameContentAs(classpathResource("ResultSetExample.txt")));
  }
}
