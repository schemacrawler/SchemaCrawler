/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.LoggingConfig;

@TestInstance(Lifecycle.PER_CLASS)
public class IOUtilityTest {

  @Test
  public void copy() throws IOException {
    final StringWriter writer = new StringWriter();
    IOUtility.copy(null, writer);
    assertThat(writer.toString(), is(""));

    final Reader reader = mock(Reader.class);
    IOUtility.copy(reader, null);
    verifyNoInteractions(reader);
  }

  @Test
  public void createTempFilePath() throws IOException {
    final Path tempFilePath = IOUtility.createTempFilePath("stem", "ext");
    assertThat(tempFilePath.getFileName().toString(), startsWith("stem"));
    assertThat(tempFilePath.getFileName().toString(), endsWith("ext"));
  }

  @BeforeAll
  public void disableLogging() throws Exception {
    // Turn off logging
    new LoggingConfig();
  }

  @Test
  public void fileExtension_path() {
    assertThat(IOUtility.getFileExtension((Path) null), is(""));
    assertThat(IOUtility.getFileExtension(Paths.get("")), is(""));
    // assertThat(IOUtility.getFileExtension(Paths.get(" ")), is(""));
    assertThat(IOUtility.getFileExtension(Paths.get(".")), is(""));
    assertThat(IOUtility.getFileExtension(Paths.get("abc")), is(""));
    assertThat(IOUtility.getFileExtension(Paths.get("abc.")), is(""));

    // assertThat(IOUtility.getFileExtension(Paths.get("abc. ")), is(" "));
    assertThat(IOUtility.getFileExtension(Paths.get("abc.xyz")), is("xyz"));
    assertThat(IOUtility.getFileExtension(Paths.get(".xyz")), is("xyz"));
  }

  @Test
  public void fileExtension_string() {
    assertThat(IOUtility.getFileExtension((String) null), is(""));
    assertThat(IOUtility.getFileExtension(""), is(""));
    assertThat(IOUtility.getFileExtension("  "), is(""));
    assertThat(IOUtility.getFileExtension("."), is(""));
    assertThat(IOUtility.getFileExtension("abc"), is(""));
    assertThat(IOUtility.getFileExtension("abc."), is(""));

    assertThat(IOUtility.getFileExtension("abc. "), is(" "));
    assertThat(IOUtility.getFileExtension("abc.xyz"), is("xyz"));
    assertThat(IOUtility.getFileExtension(".xyz"), is("xyz"));
  }

  @Test
  public void fileReadable() throws IOException {
    assertThat(IOUtility.isFileReadable(null), is(false));
    // ("." is not a file, but a directory)
    assertThat(IOUtility.isFileReadable(Paths.get(".")), is(false));

    final Path tempFile = Files.createTempFile("sc", ".dat");
    // (empty file)
    assertThat(IOUtility.isFileReadable(tempFile), is(false));
    // File is not readable
    assertThat(IOUtility.isFileReadable(tempFile.resolve("no-file")), is(false));

    Files.write(tempFile, new byte[] {65});
    assertThat(IOUtility.isFileReadable(tempFile), is(true));
  }

  @Test
  public void fileWritable() throws IOException {
    assertThat(IOUtility.isFileWritable(null), is(false));
    // ("." is not a file, but a directory)
    assertThat(IOUtility.isFileWritable(Paths.get(".")), is(false));

    final Path tempFile = IOUtility.createTempFilePath("", ".dat");
    Files.write(tempFile, new byte[] {65});
    assertThat(IOUtility.isFileWritable(tempFile), is(true));

    // Parent path is not a directory
    assertThat(IOUtility.isFileWritable(tempFile.resolve("badfile")), is(false));
    // Parent path does not exist
    assertThat(IOUtility.isFileWritable(tempFile.getParent().resolve("baddir/badfile")), is(false));
  }

  @Test
  public void readFully() throws IOException {
    assertThat(IOUtility.readFully((Reader) null), is(""));

    final Reader reader = mock(Reader.class);
    doThrow(new IOException("Exception using a mocked reader"))
        .when(reader)
        .read(any(), anyInt(), anyInt());

    assertThat(IOUtility.readFully(reader), is(""));
  }

  @Test
  public void readResourceFully() throws IOException {
    assertThat(IOUtility.readResourceFully("no-resouce"), is(""));
    assertThat(IOUtility.readResourceFully("/test-resource.txt"), startsWith("hello, world"));
  }

  @Test
  public void noClasspathResource() {
    assertThat(IOUtility.locateResource("no_resource"), is(nullValue()));
    assertThat(IOUtility.locateResource(null), is(nullValue()));
  }

  @Test
  public void classpathResourceAvailable() throws IOException {
    final String classpathResource = "/test-resource.txt";
    assertThat(IOUtility.locateResource(classpathResource), is(not(nullValue())));
    assertThat(
        IOUtility.locateResource(classpathResource).toExternalForm(), endsWith(classpathResource));
  }
}
