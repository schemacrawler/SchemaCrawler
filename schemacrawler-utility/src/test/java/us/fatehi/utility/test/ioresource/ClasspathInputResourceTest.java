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

package us.fatehi.utility.test.ioresource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
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
        "Classpath resource does not match",
        resource.getClasspathResource(),
        is(classpathResource));
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
