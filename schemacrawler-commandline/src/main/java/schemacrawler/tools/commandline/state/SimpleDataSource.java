package schemacrawler.tools.commandline.state;


import static java.util.Objects.requireNonNull;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.UserCredentials;

public class SimpleDataSource
  implements DataSource
{

  private static class MultiUseUserCredentials
    implements UserCredentials
  {
    private final String password;
    private final String user;

    MultiUseUserCredentials(final String user, final String password)
    {
      this.password = password;
      this.user = user;
    }

    @Override
    public void clearPassword()
    {
      // No action
    }

    @Override
    public String getPassword()
    {
      return password;
    }

    @Override
    public String getUser()
    {
      return user;
    }

    @Override
    public boolean hasPassword()
    {
      return password != null;
    }

    @Override
    public boolean hasUser()
    {
      return user != null;
    }
  }


  private final DatabaseConnectionSource databaseConnectionSource;

  public SimpleDataSource(final DatabaseConnectionSource databaseConnectionSource)
  {
    this.databaseConnectionSource = requireNonNull(databaseConnectionSource,
                                                   "No database connection source provided");

    resetUserCredentials();
  }

  @Override
  public Connection getConnection()
    throws SQLException
  {
    return databaseConnectionSource.get();
  }

  @Override
  public Connection getConnection(final String username, final String password)
    throws SQLException
  {
    databaseConnectionSource
      .setUserCredentials(new MultiUseUserCredentials(username, password));
    return getConnection();
  }

  @Override
  public <T> T unwrap(final Class<T> iface)
    throws SQLException
  {
    return null;
  }

  @Override
  public boolean isWrapperFor(final Class<?> iface)
    throws SQLException
  {
    return false;
  }

  @Override
  public PrintWriter getLogWriter()
    throws SQLException
  {
    return null;
  }

  @Override
  public void setLogWriter(final PrintWriter out)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported");
  }

  private void resetUserCredentials()
  {
    final UserCredentials userCredentials = databaseConnectionSource
      .getUserCredentials();
    final String user = userCredentials.getUser();
    final String password = userCredentials.getPassword();
    databaseConnectionSource
      .setUserCredentials(new MultiUseUserCredentials(user, password));
  }

  @Override
  public void setLoginTimeout(final int seconds)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported");
  }

  @Override
  public int getLoginTimeout()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported");
  }

  @Override
  public Logger getParentLogger()
    throws SQLFeatureNotSupportedException
  {
    throw new SQLFeatureNotSupportedException("Not supported");
  }

}
