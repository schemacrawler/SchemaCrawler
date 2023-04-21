/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

public class MultiUseUserCredentials implements UserCredentials {

  private final String password;
  private final String user;

  public MultiUseUserCredentials() {
    this(null, null);
  }

  public MultiUseUserCredentials(final String user, final String password) {
    this.password = password;
    this.user = user;
  }

  @Override
  public void clearPassword() {
    // No action
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUser() {
    return user;
  }

  @Override
  public boolean hasPassword() {
    return password != null;
  }

  @Override
  public boolean hasUser() {
    return user != null;
  }
}
