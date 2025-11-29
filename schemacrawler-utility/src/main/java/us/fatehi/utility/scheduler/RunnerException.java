/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.scheduler;

import java.io.Serial;

public class RunnerException extends Exception {

  @Serial private static final long serialVersionUID = -8904255341894856632L;

  public RunnerException(final Throwable cause) {
    super(cause);
  }
}
