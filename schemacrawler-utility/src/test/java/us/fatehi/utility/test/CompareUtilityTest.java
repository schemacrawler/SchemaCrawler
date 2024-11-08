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

package us.fatehi.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.CompareUtility;

public class CompareUtilityTest {

  @Test
  public void equal() {
    // Zero length
    assertThat(
        CompareUtility.compareLists(new ArrayList<String>(), new ArrayList<String>()), is(0));

    // Same length and values
    assertThat(CompareUtility.compareLists(Arrays.asList("hello"), Arrays.asList("hello")), is(0));
  }

  @Test
  public void nullArgs() {
    assertThat(CompareUtility.compareLists(null, null), is(0));
    assertThat(CompareUtility.compareLists(null, new ArrayList<String>()), is(lessThan(0)));
    assertThat(CompareUtility.compareLists(new ArrayList<String>(), null), is(greaterThan(0)));
  }

  @Test
  public void unequal() {
    // Different lengths
    assertThat(
        CompareUtility.compareLists(Arrays.asList("hello"), new ArrayList<String>()),
        is(greaterThan(0)));
    assertThat(
        CompareUtility.compareLists(new ArrayList<String>(), Arrays.asList("hello")),
        is(lessThan(0)));

    // Same length different values
    assertThat(
        CompareUtility.compareLists(Arrays.asList("zorro"), Arrays.asList("hello")),
        is(greaterThan(0)));
    assertThat(
        CompareUtility.compareLists(Arrays.asList("hello"), Arrays.asList("zorro")),
        is(lessThan(0)));
  }
}
