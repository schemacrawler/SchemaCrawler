package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Schema;
import schemacrawler.tools.integration.spring.SchemaCrawlerBean;
import dbconnector.test.TestUtility;

public class SpringIntegrationTest
{

  private ApplicationContext appContext;

  @Before
  public void setupDatabase()
  {
    appContext = new ClassPathXmlApplicationContext("context.xml");

    final DataSource dataSource = (DataSource) appContext.getBean("dataSource");
    TestUtility.setupSchema(dataSource);
  }

  @Test
  public void testSchema()
  {
    final SchemaCrawlerBean schemaCrawler = (SchemaCrawlerBean) appContext
      .getBean("schemaCrawler");
    final Schema schema = schemaCrawler.getSchema(SchemaInfoLevel.maximum);
    assertEquals(6, schema.getTables().length);
  }

}
