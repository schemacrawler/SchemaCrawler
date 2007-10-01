package schemacrawler.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Schema;
import schemacrawler.tools.integration.spring.SchemaCrawlerBean;

public class SpringIntegrationTest
{

  @Test
  public void testSchema()
  {
    final ApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml");
    SchemaCrawlerBean schemaCrawler = (SchemaCrawlerBean) appContext
      .getBean("schemaCrawler");
    Schema schema = schemaCrawler.getSchema(SchemaInfoLevel.maximum);
    assertEquals(6, schema.getTables().length);
  }

}
