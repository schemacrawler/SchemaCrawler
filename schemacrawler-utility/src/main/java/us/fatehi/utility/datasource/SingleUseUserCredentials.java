/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static us.fatehi.utility.Utility.isBlank;

public final class SingleUseUserCredentials implements UserCredentials {

  private final String user;
  private final char[] password;
  private boolean isCleared;

  public SingleUseUserCredentials() {
    user = null;
    password = null;
  }

  public SingleUseUserCredentials(final String user, final String password) {
    this.user = user;
    if (password == null) {
      this.password = null;
    } else {
      this.password = password.toCharArray();
    }
  }

  @Override
  public void clearPassword() {
    isCleared = true;
    if (hasPassword()) {
      for (int i = 0; i < password.length; i++) {
        password[i] = 0;
      }
    }
  }

  @Override
  public String getPassword() {
    if (isCleared) {
      throw new IllegalAccessError("Password has been cleared");
    }

    final String passwordString;
    if (password == null) {
      passwordString = null;
    } else {
      passwordString = new String(password);
    }

    clearPassword();

    return passwordString;
  }

  @Override
  public String getUser() {
    return user;
  }

  @Override
  public boolean hasPassword() {
    return !isCleared && password != null;
  }

  @Override
  public boolean hasUser() {
    return !isBlank(user);
  }

  @Override
  public String toString() {
    return "UserCredentials [user=\"" + user + "\", password=\"*****\"]";
  }
}
