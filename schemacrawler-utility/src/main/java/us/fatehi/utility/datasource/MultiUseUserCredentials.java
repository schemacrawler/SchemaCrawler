/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

public record MultiUseUserCredentials(String user, String password) implements UserCredentials {

  public MultiUseUserCredentials() {
    this(null, null);
  }

  @Override
  public void clearPassword() {
    // No action
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
