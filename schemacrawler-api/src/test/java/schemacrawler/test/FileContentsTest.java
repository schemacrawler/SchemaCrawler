/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    final FileContents fileContents = new FileContents(Paths.get("fakepath"));
    assertThat(fileContents.get(), is(""));
  }

  @Test
  public void nullcheck() throws IOException {
    assertThrows(NullPointerException.class, () -> new FileContents(null));

    final Path tempFilePath = IOUtility.createTempFilePath("test", ".dat");
    assertThrows(NullPointerException.class, () -> new FileContents(tempFilePath, null));
  }
}
