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
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.fatehi.utility.IOUtility.readFully;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class ClasspathInputResourceTest {

  @Test
  public void badArgs() {
    assertThrows(IOException.class, () -> new ClasspathInputResource("no_resource"));
  }

  @Test
  public void happyPath() throws IOException {
    final String classpathResource = "/test-resource.txt";
    final ClasspathInputResource resource = new ClasspathInputResource(classpathResource);
    assertThat(
        "Description does not match", resource.getDescription(), endsWith(classpathResource));
    assertThat("toString() does not match", resource.toString(), endsWith(classpathResource));
    assertThat(readFully(resource.openNewInputReader(UTF_8)), startsWith("hello, world"));
  }

  @Test
  public void nullArgs() {
    assertThrows(NullPointerException.class, () -> new ClasspathInputResource(null));
  }
}
