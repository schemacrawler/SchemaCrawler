/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.ioresource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.fatehi.utility.IOUtility.readFully;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.ioresource.FileInputResource;

public class FileInputResourceTest {

  @Test
  public void badArgs() {
    assertThrows(IOException.class, () -> new FileInputResource(Paths.get("no_resource")));
  }

  @Test
  public void happyPath() throws IOException {
    final Path fileResource = Files.createTempFile("sc", ".txt");
    Files.write(fileResource, "hello, world".getBytes(UTF_8));

    final FileInputResource resource = new FileInputResource(fileResource);
    assertThat(
        "Description does not match",
        resource.getDescription(),
        endsWith(fileResource.getFileName().toString()));
    assertThat("toString() does not match", resource.toString(), is(fileResource.toString()));
    assertThat(readFully(resource.openNewInputReader(UTF_8)), startsWith("hello, world"));
  }

  @Test
  public void nullArgs() {
    assertThrows(NullPointerException.class, () -> new FileInputResource(null));
  }
}
