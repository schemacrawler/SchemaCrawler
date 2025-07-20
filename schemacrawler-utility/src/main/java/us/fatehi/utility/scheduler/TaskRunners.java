/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.scheduler;

import static us.fatehi.utility.PropertiesUtility.getSystemConfigurationProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskRunners {

  private static final Logger LOGGER = Logger.getLogger(TaskRunners.class.getName());

  public static TaskRunner getTaskRunner(final String id, final int maxThreadsSuggested) {
    final boolean isSingleThreaded =
        Boolean.valueOf(
            getSystemConfigurationProperty("SC_SINGLE_THREADED", Boolean.FALSE.toString()));
    if (isSingleThreaded) {
      LOGGER.log(Level.CONFIG, "Loading database schema in the main thread");
      return new MainThreadTaskRunner(id);
    } else {
      LOGGER.log(Level.CONFIG, "Loading database schema using multiple threads");
      return new MultiThreadedTaskRunner(id, maxThreadsSuggested);
    }
  }
}
