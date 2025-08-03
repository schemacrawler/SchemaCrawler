/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.databaseconnector;

import schemacrawler.schemacrawler.Options;
import us.fatehi.utility.datasource.DatabaseServerType;

public interface DatabaseConnectionOptions extends Options {

  DatabaseServerType getDatabaseServerType();
}
