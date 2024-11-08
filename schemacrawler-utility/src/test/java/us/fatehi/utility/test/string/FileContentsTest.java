/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package us.fatehi.utility.test.string;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Paths.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.string.FileContents;

public class FileContentsTest {

  public static final Charset UTF_32 = Charset.forName("UTF-32");

  @Test
  public void dataFile() throws IOException {
    final Path tempFile = Files.createTempFile("test", ".txt");
    Files.write(tempFile, "hello, world".getBytes());
    assertThat(new FileContents(tempFile).get(), is("hello, world"));
  }

  @Test
  public void dataFileBadEncoding() throws IOException {
    final Path tempFile = Files.createTempFile("test", ".txt");
    Files.write(tempFile, "hello".getBytes(UTF_8));
    assertThat(new FileContents(tempFile, UTF_32).get(), is("\uFFFD\uFFFD"));
  }

  @Test
  public void dataFileMatchEncoding() throws IOException {
    final Path tempFile = Files.createTempFile("test", ".txt");
    Files.write(tempFile, "hello, world".getBytes("UTF-32"));
    assertThat(new FileContents(tempFile, UTF_32).get(), is("hello, world"));

    // Assert toString
    assertThat(
        new FileContents(tempFile, UTF_32).get(),
        is(new FileContents(tempFile, UTF_32).toString()));
  }

  @Test
  public void emptyFile() throws IOException {
    final Path tempFile = Files.createTempFile("test", ".txt");
    assertThat(new FileContents(tempFile).get(), is(""));
  }

  @Test
  public void missingFile() {
    assertThat(new FileContents(get("nofile.txt")).get(), is(""));
  }

  @Test
  public void nullArgs() {
    assertThrows(NullPointerException.class, () -> new FileContents(null));
    assertThrows(NullPointerException.class, () -> new FileContents(null, null));
    assertThrows(NullPointerException.class, () -> new FileContents(get("nofile.txt"), null));
  }
}
