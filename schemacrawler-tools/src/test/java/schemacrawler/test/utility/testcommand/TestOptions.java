/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility.testcommand;

import schemacrawler.tools.executable.CommandOptions;

public class TestOptions implements CommandOptions {

  private final String testCommandParameter;
  private final boolean usesConnection;

  public TestOptions(final boolean usesConnection, final String testCommandParameter) {
    this.usesConnection = usesConnection;
    this.testCommandParameter = testCommandParameter;
  }

  @Override
  public String toString() {
    return "TestOptions [testCommandParameter=" + testCommandParameter + "]";
  }

  public boolean usesConnection() {
    return usesConnection;
  }
}
