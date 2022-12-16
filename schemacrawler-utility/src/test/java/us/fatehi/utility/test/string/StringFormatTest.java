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

package us.fatehi.utility.test.string;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.string.StringFormat;

public class StringFormatTest {

  @Test
  public void badFormat() {
    assertThat(new StringFormat("%d", "hello").get(), is(""));
  }

  @Test
  public void happyPath() {
    assertThat(new StringFormat("").get(), is(""));
    assertThat(new StringFormat("", 1).get(), is(""));
    assertThat(new StringFormat("hello").get(), is("hello"));
    assertThat(new StringFormat("%03d", 1).get(), is("001"));
  }

  @Test
  public void nullArgs() {
    assertThat(new StringFormat(null, (String) null).get(), is(nullValue()));
    assertThat(new StringFormat("", (String) null).get(), is(""));
    assertThat(new StringFormat("%s", (String) null).get(), is("null"));
  }

  @Test
  public void string() {
    assertThat(new StringFormat("%03d", 1).get(), is(new StringFormat("%03d", 1).toString()));
  }
}
