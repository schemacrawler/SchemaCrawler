/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.offline.jdbc;

import java.nio.file.Path;
import java.sql.Connection;

public interface OfflineConnection extends Connection {

  Path getOfflineDatabasePath();
}
