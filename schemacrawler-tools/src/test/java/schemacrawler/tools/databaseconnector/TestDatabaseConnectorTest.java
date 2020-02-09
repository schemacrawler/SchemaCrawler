package schemacrawler.tools.databaseconnector;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static schemacrawler.test.utility.IsEmptyMap.emptyMap;

import org.junit.jupiter.api.Test;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.TestDatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;

public class TestDatabaseConnectorTest
{

  /**
   * NOTE: This test does not test production code, but rather test utility
   * code. However, it covers basic logic in the database connector class.
   */
  @Test
  public void testDatabaseConnector()
    throws Exception
  {
    final DatabaseConnector databaseConnector = new TestDatabaseConnector();

    final Config config = databaseConnector.getConfig();
    assertThat(config, is(notNullValue()));
    assertThat(config, aMapWithSize(4));

    final PluginCommand helpCommand = databaseConnector.getHelpCommand();
    assertThat(helpCommand, is(notNullValue()));
    assertThat(helpCommand.getName(), is("test-db"));

    assertThat(databaseConnector
                 .getDatabaseServerType()
                 .getDatabaseSystemIdentifier(), is("test-db"));

    final EnumDataTypeHelper enumDataTypeHelper =
      databaseConnector.getEnumDataTypeHelper();
    assertThat(enumDataTypeHelper
                 .getEnumDataTypeInfo(null, null, null)
                 .getEnumValues(), is(empty()));

    assertThat(databaseConnector.supportsUrl("jdbc:test-db:somevalue"),
               is(true));
    assertThat(databaseConnector.supportsUrl("jdbc:newdb:somevalue"),
               is(false));

    assertThat(databaseConnector.toString(),
               is("Database connector for test-db - Test Database"));
  }

  @Test
  public void unknownDatabaseConnector()
    throws Exception
  {
    final DatabaseConnector databaseConnector = DatabaseConnector.UNKNOWN;

    final Config config = databaseConnector.getConfig();
    assertThat(config, is(notNullValue()));
    assertThat(config, is(emptyMap()));

    final PluginCommand helpCommand = databaseConnector.getHelpCommand();
    assertThat(helpCommand, is(notNullValue()));
    assertThat(helpCommand.getName(), is(nullValue()));

    assertThat(databaseConnector
                 .getDatabaseServerType()
                 .getDatabaseSystemIdentifier(), is(nullValue()));

    final EnumDataTypeHelper enumDataTypeHelper =
      databaseConnector.getEnumDataTypeHelper();
    assertThat(enumDataTypeHelper
                 .getEnumDataTypeInfo(null, null, null)
                 .getEnumValues(), is(empty()));

    assertThat(databaseConnector.supportsUrl("jdbc:newdb:somevalue"),
               is(false));

    assertThat(databaseConnector.toString(),
               is("Database connector for unknown database system type"));
  }

}
