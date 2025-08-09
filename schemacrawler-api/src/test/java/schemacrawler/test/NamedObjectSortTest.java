/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.utility.NamedObjectSort;

public class NamedObjectSortTest {

  private class NewNamedObject implements NamedObject {

    /** */
    private static final long serialVersionUID = 6242517200200079638L;

    private final int value;
    private final String name;

    public NewNamedObject(final String name, final int value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public int compareTo(final NamedObject o) {
      if (o == null) {
        return -1;
      }
      if (o instanceof NewNamedObject) {
        return Integer.compare(value, ((NewNamedObject) o).value);
      } else {
        return -1;
      }
    }

    @Override
    public String getFullName() {
      return name;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public NamedObjectKey key() {
      return null;
    }
  }

  private class NullNamedObject implements NamedObject {

    /** */
    private static final long serialVersionUID = -5606558254139597791L;

    @Override
    public int compareTo(final NamedObject o) {
      return 1;
    }

    @Override
    public String getFullName() {
      return null;
    }

    @Override
    public String getName() {
      return null;
    }

    @Override
    public NamedObjectKey key() {
      return null;
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false, true, false, false, true})
  public void namedObjectSort(final boolean sortAlphabetically) throws Exception {

    // Set up
    final List<NamedObject> expected = createExpectedList(sortAlphabetically);

    final List<NamedObject> objects = new ArrayList<>(expected);
    Collections.shuffle(objects);
    assertThat("Test named object list is not shuffled", objects, is(not(expected)));

    // Finally - do the sort
    Collections.sort(objects, NamedObjectSort.getNamedObjectSort(sortAlphabetically));

    // Compare
    assertThat("Test named object list is not sorted", objects, is(expected));
  }

  private List<NamedObject> createExpectedList(final boolean sortAlphabetically) {

    final String[] names =
        new String[] {
          "ID",
          "FIRSTNAME",
          "LASTNAME",
          "ADDRESS1",
          "ADDRESS2",
          "CITY",
          "STATE",
          "POSTALCODE",
          "COUNTRY",
        };
    if (sortAlphabetically) {
      Arrays.sort(names);
    }

    final int length = names.length;

    final NullNamedObject nullNamedObject = new NullNamedObject();
    final List<NamedObject> expected = new ArrayList<>();
    for (int i = 0; i < length; i++) {
      expected.add(new NewNamedObject(names[i], i));
    }
    expected.add(nullNamedObject);
    expected.add(null);

    if (sortAlphabetically) {
      expected.remove(nullNamedObject);
      expected.add(0, nullNamedObject);
    }

    return expected;
  }
}
