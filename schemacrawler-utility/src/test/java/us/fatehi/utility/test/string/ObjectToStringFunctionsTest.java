/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.string;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.AccessMode;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.utility.ObjectToString;

public class ObjectToStringFunctionsTest {

  @Test
  public void arrayToList() {
    assertThat(ObjectToString.arrayToList(null), is(nullValue()));
    assertThat(ObjectToString.arrayToList(new Object()), is(nullValue()));
    assertThat(ObjectToString.arrayToList("hello, world"), is(nullValue()));
    assertThat(ObjectToString.arrayToList(AccessMode.READ), is(nullValue()));

    assertThat(ObjectToString.arrayToList(new int[] {}), is(new ArrayList<>()));
    assertThat(ObjectToString.arrayToList(new int[] {1, 2}), is(List.of(1, 2)));
    assertThat(ObjectToString.arrayToList(new HashSet<>(List.of(1, 2))), is(nullValue()));

    assertThat(ObjectToString.arrayToList(new String[] {}), is(new ArrayList<>()));
    assertThat(ObjectToString.arrayToList(new String[] {"1", "2"}), is(List.of("1", "2")));
    assertThat(ObjectToString.arrayToList(new HashSet<>(List.of("1", "2"))), is(nullValue()));
  }

  @Test
  public void classHierarchy() throws IOException {
    assertThat(ObjectToString.classHierarchy(null), is(Collections.EMPTY_LIST));

    assertThat(ObjectToString.classHierarchy(new Object()), is(List.of(Object.class)));
    assertThat(ObjectToString.classHierarchy("hello, world"), is(List.of(String.class)));
    assertThat(
        ObjectToString.classHierarchy(new FileWriter(Files.createTempFile("", "").toFile())),
        is(List.of(FileWriter.class, OutputStreamWriter.class, Writer.class)));
  }

  @Test
  public void collectionOrArrayToList() {
    assertThat(ObjectToString.collectionOrArrayToList(null), is(new ArrayList<>()));
    assertThat(ObjectToString.collectionOrArrayToList(new Object()), is(new ArrayList<>()));
    assertThat(ObjectToString.collectionOrArrayToList("hello, world"), is(new ArrayList<>()));
    assertThat(ObjectToString.collectionOrArrayToList(AccessMode.READ), is(new ArrayList<>()));

    assertThat(ObjectToString.collectionOrArrayToList(new int[] {}), is(new ArrayList<>()));
    assertThat(ObjectToString.collectionOrArrayToList(new int[] {1, 2}), is(List.of(1, 2)));
    assertThat(
        ObjectToString.collectionOrArrayToList(new HashSet<>(List.of(1, 2))), is(List.of(1, 2)));

    assertThat(ObjectToString.collectionOrArrayToList(new String[] {}), is(new ArrayList<>()));
    assertThat(
        ObjectToString.collectionOrArrayToList(new String[] {"1", "2"}), is(List.of("1", "2")));
    assertThat(
        ObjectToString.collectionOrArrayToList(new HashSet<>(List.of("1", "2"))),
        is(List.of("1", "2")));
  }

  @Test
  public void fields() throws IOException {
    assertThat(ObjectToString.fields(null), is(Collections.EMPTY_LIST));

    assertThat(ObjectToString.fields(new Object()), is(Collections.EMPTY_LIST));
    assertThat(ObjectToString.fields("hello, world"), is(Collections.EMPTY_LIST));
    assertThat(ObjectToString.fields(1), hasSize(1));
    assertThat(ObjectToString.fields(Integer.valueOf(1)), hasSize(1));
    assertThat(ObjectToString.fields(new int[] {1, 2}), is(Collections.EMPTY_LIST));
    assertThat(ObjectToString.fields(AccessMode.READ), is(Collections.EMPTY_LIST));
    assertThat(
        ObjectToString.fields(new FileWriter(Files.createTempFile("", "").toFile())), hasSize(3));
  }

  @Test
  public void isCollectionOrArray() {
    assertThat(ObjectToString.isCollectionOrArray(null), is(false));

    assertThat(ObjectToString.isCollectionOrArray(new Object()), is(false));
    assertThat(ObjectToString.isCollectionOrArray("hello, world"), is(false));
    assertThat(ObjectToString.isCollectionOrArray(true), is(false));

    assertThat(ObjectToString.isCollectionOrArray(new int[] {1, 2}), is(true));
    assertThat(ObjectToString.isCollectionOrArray(new String[] {"1", "2"}), is(true));
    assertThat(ObjectToString.isCollectionOrArray(List.of("1", "2")), is(true));
    assertThat(ObjectToString.isCollectionOrArray(new HashSet<>(List.of("1", "2"))), is(true));
  }

  @Test
  public void isPrimitive() {
    assertThat(ObjectToString.isPrimitive(null), is(false));

    assertThat(ObjectToString.isPrimitive(new Object()), is(false));
    assertThat(ObjectToString.isPrimitive(new int[] {1, 2}), is(false));
    assertThat(ObjectToString.isPrimitive(new String[] {"1", "2"}), is(false));

    assertThat(ObjectToString.isPrimitive("hello, world"), is(false));
    assertThat(ObjectToString.isPrimitive(AccessMode.READ), is(false));
    assertThat(ObjectToString.isPrimitive('a'), is(false));
    assertThat(ObjectToString.isPrimitive(Character.valueOf('a')), is(false));

    assertThat(ObjectToString.isPrimitive(1), is(true));
    assertThat(ObjectToString.isPrimitive(Integer.valueOf(1)), is(true));
    assertThat(ObjectToString.isPrimitive(1.1), is(true));
    assertThat(ObjectToString.isPrimitive(Double.valueOf(1.1)), is(true));
    assertThat(ObjectToString.isPrimitive(true), is(true));
    assertThat(ObjectToString.isPrimitive(Boolean.TRUE), is(true));
  }

  @Test
  public void isSimpleObject() {
    assertThat(ObjectToString.isSimpleObject(null), is(false));

    assertThat(ObjectToString.isSimpleObject(new Object()), is(false));
    assertThat(ObjectToString.isSimpleObject(new int[] {1, 2}), is(false));
    assertThat(ObjectToString.isSimpleObject(new String[] {"1", "2"}), is(false));

    assertThat(ObjectToString.isSimpleObject("hello, world"), is(true));
    assertThat(ObjectToString.isSimpleObject(AccessMode.READ), is(true));
    assertThat(ObjectToString.isSimpleObject('a'), is(true));
    assertThat(ObjectToString.isSimpleObject(Character.valueOf('a')), is(true));

    assertThat(ObjectToString.isSimpleObject(1), is(true));
    assertThat(ObjectToString.isSimpleObject(Integer.valueOf(1)), is(true));
    assertThat(ObjectToString.isSimpleObject(1.1), is(true));
    assertThat(ObjectToString.isSimpleObject(Double.valueOf(1.1)), is(true));
    assertThat(ObjectToString.isSimpleObject(true), is(true));
    assertThat(ObjectToString.isSimpleObject(Boolean.TRUE), is(true));
  }

  @Test
  public void objectMap() throws IOException {
    assertThat(ObjectToString.objectMap(null), is(Collections.EMPTY_MAP));

    assertThat(
        ObjectToString.objectMap(new Object()),
        is(TestObjectUtility.fakeObjectMapFor(Object.class)));
    assertThat(ObjectToString.objectMap("hello, world"), is(Collections.EMPTY_MAP));
    assertThat(ObjectToString.objectMap(1), is(Collections.EMPTY_MAP));
    assertThat(ObjectToString.objectMap(Integer.valueOf(1)), is(Collections.EMPTY_MAP));
    assertThat(ObjectToString.objectMap(new int[] {1, 2}), is(Collections.EMPTY_MAP));

    final Map<Object, Object> map = new HashMap<>();
    map.put(AccessMode.READ, AccessMode.READ);
    assertThat(ObjectToString.objectMap(map), hasEntry("READ", AccessMode.READ));

    final Map<String, Object> expectedMap = TestObjectUtility.makeTestObjectMap();
    final Map<String, Object> actualMap =
        ObjectToString.objectMap(TestObjectUtility.makeTestObject());

    final Set<String> keySet = expectedMap.keySet();
    for (final String key : keySet) {
      final Object expected = expectedMap.get(key);
      final Object actual = actualMap.get(key);
      if (actual == null) {
        continue;
      }
      final Class<? extends Object> actualClass = actual.getClass();
      if (actualClass.isEnum() || Map.class.isAssignableFrom(actualClass)) {
        assertThat("key does not match - " + key, actual.toString(), is(expected.toString()));
      } else if (Map.class.isAssignableFrom(actualClass)) {
        assertThat(
            "map does not match - " + key, ((Map<?, ?>) actual), equalTo(((Map<?, ?>) expected)));
      } else if (ObjectToString.isSimpleObject(actual)
          || ObjectToString.isCollectionOrArray(actual)) {
        assertThat("key does not match - " + key, actual, is(expected));
      }
    }
  }
}
