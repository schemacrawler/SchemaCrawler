package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Schema;
import schemacrawler.tools.ExecutionContext;
import schemacrawler.tools.ToolsExecutor;
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
    final SchemaCrawlerOptions schemaCrawlerOptions = (SchemaCrawlerOptions) appContext
      .getBean("schemaCrawlerOptions");
    final DataSource dataSource = (DataSource) appContext.getBean("dataSource");
    final Schema schema = SchemaCrawler.getSchema(dataSource,
                                                  SchemaInfoLevel.maximum,
                                                  schemaCrawlerOptions);
    assertEquals(6, schema.getTables().length);
  }

  @Test
  public void testToolsExecutorSchemaText()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final ExecutionContext executionContext = (ExecutionContext) appContext
      .getBean("executionContextForSchema");
    final DataSource dataSource = (DataSource) appContext.getBean("dataSource");

    executionContext.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    new ToolsExecutor().execute(executionContext, dataSource);

    final File outputFile = new File(outputFilename);
    Assert.assertTrue(outputFile.exists());
    Assert.assertTrue(outputFile.length() > 0);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

  @Test
  public void testToolsExecutorCountText()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final ExecutionContext executionContext = (ExecutionContext) appContext
      .getBean("executionContextForCount");
    final DataSource dataSource = (DataSource) appContext.getBean("dataSource");

    executionContext.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    new ToolsExecutor().execute(executionContext, dataSource);

    final File outputFile = new File(outputFilename);
    Assert.assertTrue(outputFile.exists());
    Assert.assertTrue(outputFile.length() > 0);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }
}
