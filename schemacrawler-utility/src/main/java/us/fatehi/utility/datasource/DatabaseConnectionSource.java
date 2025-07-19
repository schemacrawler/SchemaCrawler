/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface DatabaseConnectionSource extends AutoCloseable, Supplier<Connection> {

  boolean releaseConnection(Connection connection);

  void setFirstConnectionInitializer(Consumer<Connection> connectionInitializer);
}
