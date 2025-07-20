/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

public enum ParameterModeType {

  /** Unknown. */
  unknown("unknown"),
  /** In. */
  in("in"),
  /** In/ out. */
  inOut("in/ out"),
  /** Out. */
  out("out"),
  /** Return. */
  returnValue("return"),
  /** Return. */
  result("result");

  private final String text;

  ParameterModeType(final String text) {
    this.text = text;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return text;
  }
}
