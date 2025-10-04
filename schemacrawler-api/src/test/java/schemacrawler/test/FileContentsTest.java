/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.string.FileContents;

public class FileContentsTest {

  @Test
  public void badEncoding() throws IOException {
    final String text = "Hello, World!";

    final Path tempFilePath = IOUtility.createTempFilePath("test", ".dat");
    Files.write(tempFilePath, text.getBytes(StandardCharsets.UTF_8));

    final FileContents fileContents = new FileContents(tempFilePath, StandardCharsets.UTF_16);
    assertThat(fileContents.get(), is(not(text)));
  }

  @Test
  public void happyPath() throws IOException {
    final String text = "Hello, World!";

    final Path tempFilePath = IOUtility.createTempFilePath("test", ".dat");
    Files.write(tempFilePath, text.getBytes(StandardCharsets.UTF_8));

    final FileContents fileContents = new FileContents(tempFilePath);
    assertThat(fileContents.get(), is(text));
  }

  @Test
  public void notReadable() throws IOException {
    final FileContents fileContents = new FileContents(Path.of("fakepath"));
    assertThat(fileContents.get(), is(""));
  }

  @Test
  public void nullcheck() throws IOException {
    assertThrows(NullPointerException.class, () -> new FileContents(null));

    final Path tempFilePath = IOUtility.createTempFilePath("test", ".dat");
    assertThrows(NullPointerException.class, () -> new FileContents(tempFilePath, null));
  }
}
