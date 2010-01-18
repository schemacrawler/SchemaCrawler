/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.schemacrawler;


import sf.util.Utility;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class BaseDatabaseConnectionOptions
  implements ConnectionOptions {

  private static final long serialVersionUID = -8141436553988174836L;

  private static final Logger LOGGER = Logger
    .getLogger(BaseDatabaseConnectionOptions.class.getName());

  private String user;
  private String password;

  public final Connection createConnection()
    throws SchemaCrawlerException {
    if (user == null) {
      LOGGER.log(Level.WARNING, "Database user is not provided");
    }
    if (password == null) {
      LOGGER.log(Level.WARNING, "Database password is not provided");
    }
    try {
      return DriverManager.getConnection(getConnectionUrl(), user, password);
    }
    catch (final SQLException e) {
      throw new SchemaCrawlerException(String
        .format("Could not connect to %s, for user %s",
                getConnectionUrl(),
                user), e);
    }
  }

  public final Driver getJdbcDriver() {
    try {
      return DriverManager.getDriver(getConnectionUrl());
    }
    catch (final SQLException e) {
      LOGGER.log(Level.WARNING,
                 "Could not get a database driver for database connection URL "
                   + getConnectionUrl());
      return null;
    }
  }

  public final String getPassword() {
    return password;
  }

  public final String getUser() {
    return user;
  }

  public final void setPassword(final String password) {
    this.password = password;
  }

  public final void setUser(final String user) {
    this.user = user;
  }

  @Override
  public final String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("driver=")
      .append(getJdbcDriver().getClass().getName())
      .append(Utility.NEWLINE);
    builder.append("url=")
      .append(getConnectionUrl())
      .append(Utility.NEWLINE);
    builder.append("user=")
      .append(getUser())
      .append(Utility.NEWLINE);
    return builder.toString();
  }

  protected static final void loadJdbcDriver(final String jdbcDriverClassName)
    throws SchemaCrawlerException {
    try {
      Class.forName(jdbcDriverClassName);
    }
    catch (final Exception e) {
      throw new SchemaCrawlerException("Could not load JDBC driver, "
        + jdbcDriverClassName);
    }
  }

}
