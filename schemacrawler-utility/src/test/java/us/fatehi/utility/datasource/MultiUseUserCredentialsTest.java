/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class MultiUseUserCredentialsTest {

  @Test
  void testUserAndPassword() {
    MultiUseUserCredentials credentials = new MultiUseUserCredentials("user", "password");
    assertThat(credentials.user(), is("user"));
    assertThat(credentials.password(), is("password"));
    assertThat(credentials.hasUser(), is(true));
    assertThat(credentials.hasPassword(), is(true));
  }

  @Test
  void testNoUserAndPassword() {
    MultiUseUserCredentials credentials = new MultiUseUserCredentials();
    assertThat(credentials.user(), is(nullValue()));
    assertThat(credentials.password(), is(nullValue()));
    assertThat(credentials.hasUser(), is(false));
    assertThat(credentials.hasPassword(), is(false));
  }

  @Test
  void testUserOnly() {
    MultiUseUserCredentials credentials = new MultiUseUserCredentials("user", null);
    assertThat(credentials.user(), is("user"));
    assertThat(credentials.password(), is(nullValue()));
    assertThat(credentials.hasUser(), is(true));
    assertThat(credentials.hasPassword(), is(false));
  }

  @Test
  void testPasswordOnly() {
    MultiUseUserCredentials credentials = new MultiUseUserCredentials(null, "password");
    assertThat(credentials.user(), is(nullValue()));
    assertThat(credentials.password(), is("password"));
    assertThat(credentials.hasUser(), is(false));
    assertThat(credentials.hasPassword(), is(true));
  }

  @Test
  void testClearPassword() {
    MultiUseUserCredentials credentials = new MultiUseUserCredentials("user", "password");
    credentials.clearPassword();
    assertThat(credentials.user(), is("user"));
    // The password should still be the as clearPassword does nothing
    assertThat(credentials.password(), is("password"));
  }
}
