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
  },
  terminate_system {
    @Override
    public void dispatch() {
      LOGGER.log(Level.SEVERE, dispatchMessage);
      System.exit(1);
    }
  },
  ;

  private static final Logger LOGGER = Logger.getLogger(LintDispatch.class.getName());

  private static final String dispatchMessage = "Too many schema lints were found";

  public abstract void dispatch();
}
