/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.ioresource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.ioresource.ClasspathInputResource;
import us.fatehi.utility.ioresource.FileInputResource;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.ioresource.InputResourceUtility;

public class InputResourceUtilityTest {

  @Test
  public void badArgs() {
    assertThat(InputResourceUtility.createInputResource("bad-resource").isPresent(), is(false));
  }

  @Test
  public void classpath() {
    final InputResource inputResource =
        InputResourceUtility.createInputResource("/test-resource.txt").get();
    assertThat(inputResource, is(instanceOf(ClasspathInputResource.class)));
    assertThat(inputResource.getDescription(), endsWith("/test-resource.txt"));
  }

  @Test
  public void file() throws IOException {
    final Path fileResource = Files.createTempFile("sc", ".txt");
    Files.write(fileResource, "hello, world".getBytes(UTF_8));

    final InputResource inputResource =
        InputResourceUtility.createInputResource(fileResource.toString()).get();
    assertThat(inputResource, is(instanceOf(FileInputResource.class)));
    assertThat(inputResource.getDescription(), is(fileResource.toString()));
  }

  @Test
  public void nullArgs() {
    assertThat(InputResourceUtility.createInputResource(null).isPresent(), is(false));
  }
}
