/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.test.ioresource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.newBufferedReader;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.fatehi.utility.IOUtility.readFully;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.ioresource.FileOutputResource;

public class FileOutputResourceTest {

  @Test
  public void happyPath() throws IOException {
    final Path tempFile = createTempFile("sc", ".txt");

    final FileOutputResource outputResource = new FileOutputResource(tempFile);
    assertThat(outputResource.getOutputFile(), is(tempFile));
    assertThat(outputResource.toString(), is(tempFile.toString()));
    assertThat(outputResource.getDescription(), is(tempFile.toString()));

    final Writer writer = outputResource.openNewOutputWriter(UTF_8, false);
    writer.write("hello, world");
    writer.close();

    assertThat(readFully(newBufferedReader(tempFile)), is("hello, world"));
  }

  @Test
  public void append() throws IOException {
    final Path tempFile = createTempFile("sc", ".txt");
    Files.write(tempFile, "hello,".getBytes(UTF_8));

    final FileOutputResource outputResource = new FileOutputResource(tempFile);
    assertThat(outputResource.getOutputFile(), is(tempFile));
    assertThat(outputResource.toString(), is(tempFile.toString()));
    assertThat(outputResource.getDescription(), is(tempFile.toString()));

    final Writer writer = outputResource.openNewOutputWriter(UTF_8, true);
    writer.write(" world");
    writer.close();

    assertThat(readFully(newBufferedReader(tempFile)), is("hello, world"));
  }

  @Test
  public void nullArgs() {
    assertThrows(NullPointerException.class, () -> new FileOutputResource(null));
  }
}
