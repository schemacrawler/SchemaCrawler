package schemacrawler.schema;


import java.io.Serializable;
import java.time.LocalDateTime;

public interface CrawlHeaderInfo
  extends Serializable
{

  /**
   * Gets the timestamp of when the database was crawled.
   *
   * @return Timestamp
   */
  LocalDateTime getCrawlTimestamp();

  /**
   * Gets the name of the RDBMS vendor and product.
   *
   * @return Name of the RDBMS vendor and product
   */
  String getDatabaseInfo();

  /**
   * Gets the name of the JDBC driver.
   *
   * @return Driver name
   */
  String getJdbcDriverInfo();

  /**
   * Gets the SchemaCrawler version.
   *
   * @return SchemaCrawler version
   */
  String getSchemaCrawlerInfo();

  /**
   * Gets the title.
   *
   * @return Title
   */
  String getTitle();

}
