/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.state;

import java.util.function.Supplier;
import java.util.logging.Level;

import java.util.logging.Logger;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import us.fatehi.utility.string.ObjectToStringFormat;

public final class StateUtility {

  private static final Logger LOGGER = Logger.getLogger(StateUtility.class.getName());

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
