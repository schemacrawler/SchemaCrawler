package schemacrawler.schema;


import java.io.Serializable;

public interface JdbcDriverInfo
  extends Serializable
{
  /**
   * Database connection URL.
   * 
   * @return Database connection URL.
   */
  String getConnectionUrl();

  /**
   * Class name of the JDBC driver.
   * 
   * @return Class name of the JDBC driver.
   */
  String getDriverClassName();

  /**
   * Name of the driver.
   * 
   * @return Driver name
   */
  String getDriverName();

  /**
   * Gets all the JDBC driver properties, and their values.
   */
  JdbcDriverProperty[] getDriverProperties();

  /**
   * Driver version.
   * 
   * @return Driver version.
   */
  String getDriverVersion();

}
