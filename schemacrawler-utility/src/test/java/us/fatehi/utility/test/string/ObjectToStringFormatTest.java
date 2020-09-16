/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.regex.Pattern.DOTALL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.string.ObjectToStringFormat;

public class ObjectToStringFormatTest {

  @Test
  public void happyPath() {
    assertThat(new ObjectToStringFormat("hello, world").get(), is("hello, world"));
    // Test toString
    assertThat(
        new ObjectToStringFormat("hello, world").get(),
        is(new ObjectToStringFormat("hello, world").toString()));

    final List<String> list = Arrays.asList("one", "two", "three");
    assertThat(new ObjectToStringFormat(list).get(), is("one, two, three"));

    final Map<String, Integer> map = new HashMap<>();
    map.put("one", 1);
    map.put("two", 2);
    map.put("three", 3);
    assertThat(
        new ObjectToStringFormat(map).get().replace(System.lineSeparator(), "\n"),
        is("\none: 1\nthree: 3\ntwo: 2"));

    assertThat(
        new ObjectToStringFormat(Instant.now()).get(),
        matchesPattern(Pattern.compile(".*nanos: \\d.*seconds: \\d.*", DOTALL)));
  }

  @Test
  public void nullArgs() {
    assertThat(new ObjectToStringFormat(null).get(), is(""));
  }
}
