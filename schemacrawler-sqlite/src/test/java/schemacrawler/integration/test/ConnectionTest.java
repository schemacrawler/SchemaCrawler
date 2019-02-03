package schemacrawler.integration.test;

import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.databaseconnector.SingleUseUserCredentials;
import schemacrawler.tools.sqlite.SQLiteDatabaseConnector;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ConnectionTest
{

  @Test
  public void createDataSourceFromNoFile()
      throws Exception
  {
    final Config config = new Config();
    config.put("server", "sqlite");
    config.put("database", "missing.db");

    final DataSource dataSource = new SQLiteDatabaseConnector()
        .newDatabaseConnectionOptions(new SingleUseUserCredentials(), config);
    assertThrows(SQLException.class, () -> dataSource.getConnection());
  }

}
