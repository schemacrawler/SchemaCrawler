/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
