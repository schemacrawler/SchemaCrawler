package schemacrawler.test;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.TestDatabaseConnector;

public class TestDatabaseConnectorTest
{

  @Test
  public void testDatabaseConnector()
    throws Exception
  {
    final TestDatabaseConnector databaseConnector =
      new TestDatabaseConnector();

    final Config config = databaseConnector.getConfig();
    assertThat(config, is(notNullValue()));
  }

}
