/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.lint;

import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;

public enum LintDispatch {
  none {
    @Override
    public void dispatch() {
      LOGGER.log(Level.FINE, dispatchMessage);
    }
  },
  write_err {
    @Override
    public void dispatch() {
      LOGGER.log(Level.CONFIG, dispatchMessage);
      System.err.println(dispatchMessage);
    }
  },
  throw_exception {
    @Override
    public void dispatch() {
      LOGGER.log(Level.WARNING, dispatchMessage);
      throw new ExecutionRuntimeException(dispatchMessage);
    }
  };

  private static final Logger LOGGER = Logger.getLogger(LintDispatch.class.getName());

  private static final String dispatchMessage = "Too many schema lints were found";

  public abstract void dispatch();
}
