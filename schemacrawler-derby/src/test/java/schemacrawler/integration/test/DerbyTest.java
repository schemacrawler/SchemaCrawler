package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.server.derby.DerbyDatabaseConnector;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

@ContextConfiguration(locations = {
  "classpath:test-derby-context.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class DerbyTest
{

  @Autowired
  private DataSource dataSource;

  @Test
  public void testConnection()
    throws Exception
  {
    assertNotNull(dataSource);
    final Connection connection = getConnection();
    assertNotNull(connection);
    assertEquals("org.apache.derby.impl.jdbc.EmbedConnection", connection
      .getClass().getName());
  }

  @Ignore
  @Test
  public void testDerbyWithConnection()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new DerbyDatabaseConnector()
      .getDatabaseSystemConnector().getSchemaCrawlerOptions(InfoLevel.maximum);
    final Catalog catalog = SchemaCrawlerUtility
      .getCatalog(getConnection(), schemaCrawlerOptions);
    assertNotNull(catalog);

    assertEquals(12, catalog.getSchemas().size());
    final Schema schema = catalog.getSchema("PUBLIC.BOOKS");
    assertNotNull(schema);

    assertEquals(6, catalog.getTables(schema).size());
    final Table table = catalog.getTable(schema, "AUTHORS");
    assertNotNull(table);

    assertEquals(1, table.getTriggers().size());
    assertNotNull(table.getTrigger("TRG_AUTHORS"));

  }

  private Connection getConnection()
    throws SQLException
  {
    return dataSource.getConnection();
  }

}
