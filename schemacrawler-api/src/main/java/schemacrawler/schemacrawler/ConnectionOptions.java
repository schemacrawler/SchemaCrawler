package schemacrawler.schemacrawler;


import java.sql.Driver;
import java.sql.SQLException;

import javax.sql.DataSource;

public interface ConnectionOptions
  extends Options, DataSource
{

  String getConnectionUrl();

  Driver getJdbcDriver()
    throws SQLException;

  String getUser();

  void setPassword(final String password);

  void setUser(final String user);

}
