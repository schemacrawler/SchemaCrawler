package schemacrawler.test.utility;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import us.fatehi.utility.datasource.DatabaseConnectionSource;

public final class DataSourceConnectionSource implements DatabaseConnectionSource {

  private final DataSource dataSource;
  private final String connectionUrl;

  public DataSourceConnectionSource(final String connectionUrl, final DataSource dataSource) {
    this.dataSource = requireNonNull(dataSource, "Data source not provided");
    this.connectionUrl = connectionUrl;
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
      fail(e);
      return false;
    }
    return true;
  }
}
