/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.ioresource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.newBufferedReader;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static us.fatehi.utility.IOUtility.readFully;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.ioresource.ConsoleOutputResource;

public class ConsoleOutputResourceTest {

  private Path tempFile;

  @AfterEach
  public void afterEach() throws Exception {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
  }

  @BeforeEach
  public void beforeEach() throws Exception {
    tempFile = createTempFile("sc", ".txt");
    System.setOut(new PrintStream(new FileOutputStream(tempFile.toFile())));
  }

  @Test
  public void happyPath() throws IOException {
    final ConsoleOutputResource outputResource = new ConsoleOutputResource();
    assertThat(outputResource.toString(), is("<console>"));
    assertThat(outputResource.getDescription(), is("<console>"));

    final Writer writer = outputResource.openNewOutputWriter(UTF_8, true);
    writer.write("hello, world");
    writer.close();

    assertThat(readFully(newBufferedReader(tempFile)), is("hello, world"));
  }
}
