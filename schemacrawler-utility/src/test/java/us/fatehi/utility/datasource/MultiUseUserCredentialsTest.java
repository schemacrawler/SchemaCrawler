/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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
