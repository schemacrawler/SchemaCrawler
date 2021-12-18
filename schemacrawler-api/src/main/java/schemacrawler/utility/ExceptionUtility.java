/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.utility;

import static us.fatehi.utility.Utility.isBlank;

import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.schemacrawler.exceptions.WrappedSQLException;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class ExceptionUtility {

  public static String makeExceptionMessage(final String message, final Throwable cause) {
    if (cause == null
        || cause instanceof SchemaCrawlerException
        || cause instanceof WrappedSQLException) {
      return message;
    }
    if (isBlank(message)) {
      return cause.getMessage();
    }
    return message + ": " + cause.getMessage();
  }

  private ExceptionUtility() {
    // Prevent instantiation
  }
}
