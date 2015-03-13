package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:derby-context.xml")
public class DerbyTest
  extends BaseExecutableTest
{

  @Autowired
  private DataSource dataSource;

  @Test
  public void testDataSource()
    throws Exception
  {
    assertNotNull(dataSource);
    final Connection connection = getConnection();
    assertNotNull(connection);
    assertEquals("org.apache.derby.impl.jdbc.EmbedConnection", connection
      .getClass().getName());
  }

  @Test
  public void testDerbyWithConnection()
    throws Exception
  {
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setSchemaInclusionRule(new RegularExpressionInclusionRule("BOOKS"));
    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setHideIndexNames(true);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(options);
    executable
      .setAdditionalConfiguration(new SchemaTextOptionsBuilder(textOptions)
        .toConfig());

    executeExecutable(executable, "text", "testDerbyWithConnection.txt");
  }

  @Override
  protected Connection getConnection()
    throws SchemaCrawlerException
  {
    try
    {
      return dataSource.getConnection();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }
  }

}
