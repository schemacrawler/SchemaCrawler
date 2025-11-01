/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.string;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

    final List<String> list = List.of("one", "two", "three");
    assertThat(new ObjectToStringFormat(list).get(), is("[\"one\", \"two\", \"three\"]"));

    final Map<String, Integer> map = new HashMap<>();
    map.put("one", 1);
    map.put("two", 2);
    map.put("three", 3);
    assertThat(
        new ObjectToStringFormat(map).get().replaceAll("\\R", "\n").trim(),
        is("{\n   \n\"one\": 1,\n   \n\"three\": 3,\n   \n\"two\": 2\n  \n}"));

    assertThat(
        new ObjectToStringFormat(new SomeClass("hello, world", 42))
            .get()
            .replaceAll("\\R", "\n")
            .trim(),
        is(
            "{\n"
                + "   \n"
                + "\"@object\":"
                + " \"us.fatehi.utility.test.string.ObjectToStringFormatTest$SomeClass\",\n"
                + "   \n"
                + "\"integer\": 42,\n"
                + "   \n"
                + "\"string\": \"hello, world\"\n"
                + "  \n"
                + "}"));
  }

  @Test
  public void nullArgs() {
    assertThat(new ObjectToStringFormat(null).get(), is(""));
  }
}
