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
