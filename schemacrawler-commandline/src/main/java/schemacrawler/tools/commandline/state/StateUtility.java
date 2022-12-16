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

package schemacrawler.tools.commandline.state;

import java.util.function.Supplier;
import java.util.logging.Level;

import java.util.logging.Logger;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import us.fatehi.utility.string.ObjectToStringFormat;

public final class StateUtility {

  private static final Logger LOGGER =
      Logger.getLogger(StateUtility.class.getName());

  public static void logState(final ShellState state, final boolean showlog) {
    if (state == null) {
      return;
    }

    if (!state.isConnected()) {
      log(Level.CONFIG, () -> "No database connection available", showlog);
    }

    final SchemaCrawlerOptions schemaCrawlerOptions = state.getSchemaCrawlerOptions();
    if (schemaCrawlerOptions != null) {
      log(Level.CONFIG, new ObjectToStringFormat(schemaCrawlerOptions), showlog);
    }

    if (state.getSchemaRetrievalOptions() != null) {
      final SchemaRetrievalOptions schemaRetrievalOptions = state.getSchemaRetrievalOptions();
      log(Level.CONFIG, new ObjectToStringFormat(schemaRetrievalOptions), showlog);
    }

    log(Level.FINE, new ObjectToStringFormat(state.getConfig()), showlog);
  }

  private static void log(final Level level, final Supplier<String> toLog, final boolean showlog) {
    if (showlog) {
      System.out.println(toLog.get());
    } else {
      LOGGER.log(level, toLog);
    }
  }
}
