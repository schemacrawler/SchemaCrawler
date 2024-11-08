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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.string.ObjectToStringFormat;

public class ObjectToStringFormatTest {

  static class SomeClass {
    private String string;
    private int integer;

    public SomeClass(final String string, final int integer) {
      this.string = string;
      this.integer = integer;
    }

    public int getInteger() {
      return integer;
    }

    public String getString() {
      return string;
    }

    public void setInteger(final int integer) {
      this.integer = integer;
    }

    public void setString(final String string) {
      this.string = string;
    }
  }

  @Test
  public void happyPath() {
    assertThat(new ObjectToStringFormat("hello, world").get(), is("hello, world"));
    // Test toString
    assertThat(
        new ObjectToStringFormat("hello, world").get(),
        is(new ObjectToStringFormat("hello, world").toString()));

    final List<String> list = Arrays.asList("one", "two", "three");
    assertThat(new ObjectToStringFormat(list).get(), is("[\"one\", \"two\", \"three\"]"));

    final Map<String, Integer> map = new HashMap<>();
    map.put("one", 1);
    map.put("two", 2);
    map.put("three", 3);
    assertThat(
        new ObjectToStringFormat(map).get().replace(System.lineSeparator(), "\n"),
        is("{\n  \"one\": 1,\n  \"three\": 3,\n  \"two\": 2\n}"));

    assertThat(
        new ObjectToStringFormat(new SomeClass("hello, world", 42)).get().replaceAll("\\R", ""),
        is(
            "{  \"@object\": \"us.fatehi.utility.test.string.ObjectToStringFormatTest$SomeClass\",  "
                + "\"integer\": 42,  \"string\": \"hello, world\"}"));
  }

  @Test
  public void nullArgs() {
    assertThat(new ObjectToStringFormat(null).get(), is(""));
  }
}
