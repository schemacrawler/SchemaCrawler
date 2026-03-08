/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.offline.connectionsource;

import java.nio.file.Path;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import schemacrawler.tools.offline.jdbc.OfflineConnectionUtility;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@UtilityMarker
public class OfflineConnectionSourceUtility {

  public static DatabaseConnectionSource newOfflineDatabaseConnectionSource(
      final Path offlineDatabasePath) {
    final OfflineConnection offlineConnection =
        OfflineConnectionUtility.newOfflineConnection(offlineDatabasePath);
    return new OfflineDatabaseConnectionSource(offlineConnection);
  }

  private OfflineConnectionSourceUtility() {
    // Prevent instantiation
  }
}
