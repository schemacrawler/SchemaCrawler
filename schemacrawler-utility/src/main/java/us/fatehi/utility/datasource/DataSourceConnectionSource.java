package us.fatehi.utility.datasource;

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

final class DataSourceConnectionSource implements DatabaseConnectionSource {

  private static final Logger LOGGER = Logger.getLogger(DataSourceConnectionSource.class.getName());

  private static String buildConnectionUrl(final DataSource dataSource) {
    try (final Connection connection = dataSource.getConnection(); ) {
      return connection.getMetaData().getURL();
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, "Could not obtain database connection URL", e);
      return null;
    }
  }

  private final DataSource dataSource;
  private final String connectionUrl;

  public DataSourceConnectionSource(final DataSource dataSource) {
    this.dataSource = requireNonNull(dataSource, "Data source not provided");
    this.connectionUrl = buildConnectionUrl(dataSource);
  }

  @Override
  public void close() throws Exception {
    if (dataSource instanceof Closeable) {
      ((Closeable) dataSource).close();
    }
  }

  @Override
  public Connection get() {
    try {
      return dataSource.getConnection();
    } catch (final SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getConnectionUrl() {
    return connectionUrl;
  }

  @Override
  public boolean releaseConnection(final Connection connection) {
    try {
      connection.close();
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, "Could not close database connection", e);
      return false;
    }
    return true;
  }
}
