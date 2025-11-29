/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static us.fatehi.utility.Utility.trimToEmpty;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.function.Supplier;

/** Allows a main method to exit without a stack trace, but with an exit code */
public class SystemExitException extends RuntimeException {

  @Serial private static final long serialVersionUID = -6142850519050179767L;

  private final int exitCode;
  private final String message;
  private final Supplier<String> messagePrinter;

  public SystemExitException(final int exitCode, final String message) {
    super(message);
    this.exitCode = exitCode;
    this.message = trimToEmpty(message);
    messagePrinter = () -> "Exit code %d: %s".formatted(exitCode, message);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }

  public int getExitCode() {
    return exitCode;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public void printStackTrace(final PrintStream s) {
    if (s == null) {
      return;
    }
    s.println(messagePrinter.get());
    s.flush();
  }

  @Override
  public void printStackTrace(final PrintWriter s) {
    if (s == null) {
      return;
    }
    s.println(messagePrinter.get());
    s.flush();
  }

  @Override
  public String toString() {
    return messagePrinter.get();
  }
}
