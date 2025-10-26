/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import us.fatehi.utility.property.PropertyName;

public class PropertyNameTest {

  @Test
  public void compare() {
    final PropertyName propertyName1 = new PropertyName("hello1", "world");
    final PropertyName propertyName2 = new PropertyName("hello", "  ");
    assertThat(propertyName1.compareTo(propertyName2), is(equalTo(1)));

    assertThat(propertyName1.compareTo(null), is(equalTo(-1)));
  }

  @Test
  public void testString() {
    final PropertyName propertyName1 = new PropertyName("hello", "world");
    assertThat(propertyName1.getName(), is(equalTo("hello")));
    assertThat(propertyName1.getDescription(), is(equalTo("world")));
    assertThat(propertyName1.toString(), is(equalTo("hello - world")));

    final PropertyName propertyName2 = new PropertyName("hello", "  ");
    assertThat(propertyName2.getName(), is(equalTo("hello")));
    assertThat(propertyName2.getDescription(), is(equalTo("")));
    assertThat(propertyName2.toString(), is(equalTo("hello")));
  }
}
