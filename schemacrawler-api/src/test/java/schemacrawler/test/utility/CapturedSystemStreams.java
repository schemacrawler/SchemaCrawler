/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

public class CapturedSystemStreams {

  private final TestOutputStream err;
  private final TestOutputStream out;

  CapturedSystemStreams(final TestOutputStream out, final TestOutputStream err) {
    this.err = err;
    this.out = out;
  }

  public TestOutputCapture err() {
    return err;
  }

  public TestOutputCapture out() {
    return out;
  }
}
