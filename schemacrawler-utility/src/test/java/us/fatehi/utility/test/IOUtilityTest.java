/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.IOUtility;

public class IOUtilityTest {

  @Test
  public void fileExtension_path() {
    assertThat(IOUtility.getFileExtension((Path) null), is(""));
    assertThat(IOUtility.getFileExtension(Paths.get("")), is(""));
    // assertThat(IOUtility.getFileExtension(Paths.get("  ")), is(""));
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
}
