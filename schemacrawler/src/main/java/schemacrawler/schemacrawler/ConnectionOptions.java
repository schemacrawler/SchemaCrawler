package schemacrawler.schemacrawler;


import java.sql.Connection;
import java.sql.Driver;

public interface ConnectionOptions
  extends Options {

  Connection createConnection()
    throws SchemaCrawlerException;

  String getConnectionUrl();

  Driver getJdbcDriver();

  String getPassword();

  String getUser();

  void setPassword(final String password);

  void setUser(final String user);

}
