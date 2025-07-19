/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
    assertThat(credentials.getUser(), is("user"));
    assertThat(credentials.getPassword(), is("password"));
    assertThat(credentials.hasUser(), is(true));
    assertThat(credentials.hasPassword(), is(true));
  }

  @Test
  void testNoUserAndPassword() {
    MultiUseUserCredentials credentials = new MultiUseUserCredentials();
    assertThat(credentials.getUser(), is(nullValue()));
    assertThat(credentials.getPassword(), is(nullValue()));
    assertThat(credentials.hasUser(), is(false));
    assertThat(credentials.hasPassword(), is(false));
  }

  @Test
  void testUserOnly() {
    MultiUseUserCredentials credentials = new MultiUseUserCredentials("user", null);
    assertThat(credentials.getUser(), is("user"));
    assertThat(credentials.getPassword(), is(nullValue()));
    assertThat(credentials.hasUser(), is(true));
    assertThat(credentials.hasPassword(), is(false));
  }

  @Test
  void testPasswordOnly() {
    MultiUseUserCredentials credentials = new MultiUseUserCredentials(null, "password");
    assertThat(credentials.getUser(), is(nullValue()));
    assertThat(credentials.getPassword(), is("password"));
    assertThat(credentials.hasUser(), is(false));
    assertThat(credentials.hasPassword(), is(true));
  }

  @Test
  void testClearPassword() {
    MultiUseUserCredentials credentials = new MultiUseUserCredentials("user", "password");
    credentials.clearPassword();
    assertThat(credentials.getUser(), is("user"));
    // The password should still be the as clearPassword does nothing
    assertThat(credentials.getPassword(), is("password"));
  }
}
