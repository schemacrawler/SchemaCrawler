/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.string;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.AccessMode;
import org.junit.jupiter.api.Test;
import us.fatehi.test.utility.TestObject;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.utility.ObjectToString;

public class ObjectToStringTest {

  @Test
  public void listOrObjectToString() {
    assertThat(ObjectToString.listOrObjectToString(null), is("null"));

    assertThat(
        ObjectToString.listOrObjectToString(new Object()).replaceAll("\\R", ""),
        containsString(Object.class.getName()));
    assertThat(ObjectToString.listOrObjectToString(new int[] {1, 2}), is("1, 2"));
    assertThat(ObjectToString.listOrObjectToString(new String[] {"1", "2"}), is("1, 2"));

    assertThat(ObjectToString.listOrObjectToString("hello, world"), is("hello, world"));
    assertThat(ObjectToString.listOrObjectToString(AccessMode.READ), is("READ"));
    assertThat(ObjectToString.listOrObjectToString('a'), is("a"));
    assertThat(ObjectToString.listOrObjectToString(Character.valueOf('a')), is("a"));

    assertThat(ObjectToString.listOrObjectToString(1), is("1"));
    assertThat(ObjectToString.listOrObjectToString(Integer.valueOf(1)), is("1"));
    assertThat(ObjectToString.listOrObjectToString(1.1), is("1.1"));
    assertThat(ObjectToString.listOrObjectToString(Double.valueOf(1.1)), is("1.1"));
    assertThat(ObjectToString.listOrObjectToString(true), is("true"));
    assertThat(ObjectToString.listOrObjectToString(Boolean.TRUE), is("true"));

    assertThat(
        ObjectToString.listOrObjectToString(TestObjectUtility.makeTestObject())
            .replaceAll("\\R", ""),
        containsString(TestObject.class.getName()));
  }

  @Test
  public void toStringTest() {
    assertThat(ObjectToString.toString(null), is("null"));

    final String json =
        """
        {
          "@object": "java.lang.Object"
        }\
        """
            .replaceAll("\\R", "\n")
            .stripIndent();
    assertThat(ObjectToString.toString(new Object()).replaceAll("\\R", "\n").strip(), is(json));
    assertThat(ObjectToString.toString(new int[] {1, 2}), is("[1, 2]"));
    assertThat(ObjectToString.toString(new String[] {"1", "2"}), is("[\"1\", \"2\"]"));

    assertThat(ObjectToString.toString("hello, world"), is("hello, world"));
    assertThat(ObjectToString.toString(AccessMode.READ), is("READ"));
    assertThat(ObjectToString.toString('a'), is("a"));
    assertThat(ObjectToString.toString(Character.valueOf('a')), is("a"));

    assertThat(ObjectToString.toString(1), is("1"));
    assertThat(ObjectToString.toString(Integer.valueOf(1)), is("1"));
    assertThat(ObjectToString.toString(1.1), is("1.1"));
    assertThat(ObjectToString.toString(Double.valueOf(1.1)), is("1.1"));
    assertThat(ObjectToString.toString(true), is("true"));
    assertThat(ObjectToString.toString(Boolean.TRUE), is("true"));

    final String testObjectJson =
        """
        {
          "@object": "us.fatehi.test.utility.TestObject",
          "integerList": [1, 1, 2, 3, 5, 8],
          "map":   {
            "1": "a",
            "2": "b",
            "3": "c"
          },
          "nullValue": null,
          "objectArray": ["a", "b", "c"],
          "plainString": "hello world",
          "primitiveArray": [1, 1, 2, 3, 5, 8],
          "primitiveBoolean": true,
          "primitiveDouble": 99.99,
          "primitiveEnum": "READ",
          "primitiveInt": 99,
          "subObject": "."
        }\
        """
            .replaceAll("\\R", "\n")
            .stripIndent();
    assertThat(
        ObjectToString.toString(TestObjectUtility.makeTestObject()).replaceAll("\\R", "\n").strip(),
        is(testObjectJson));
  }
}
