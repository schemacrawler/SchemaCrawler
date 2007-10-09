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
import schemacrawler.tools.Executable;
import schemacrawler.tools.datatext.DataTextFormatOptions;
import schemacrawler.tools.operation.OperationOptions;
import schemacrawler.tools.schematext.SchemaTextOptions;
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
  public void testExecutableForCount()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<OperationOptions> executable = (Executable<OperationOptions>) appContext
      .getBean("executableForCount");
    final DataSource dataSource = (DataSource) appContext.getBean("dataSource");

    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executable.execute(dataSource);

    final File outputFile = new File(outputFilename);
    Assert.assertTrue(outputFile.exists());
    Assert.assertTrue(outputFile.length() > 0);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

  @Test
  public void testExecutableForFreeMarker()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<SchemaTextOptions> executable = (Executable<SchemaTextOptions>) appContext
      .getBean("executableForFreeMarker");
    final DataSource dataSource = (DataSource) appContext.getBean("dataSource");

    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executable.execute(dataSource);

    final File outputFile = new File(outputFilename);
    Assert.assertTrue(outputFile.exists());
    Assert.assertTrue(outputFile.length() > 0);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

  @Test
  public void testExecutableForQuery()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<DataTextFormatOptions> executable = (Executable<DataTextFormatOptions>) appContext
      .getBean("executableForQuery");
    final DataSource dataSource = (DataSource) appContext.getBean("dataSource");

    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executable.execute(dataSource);

    final File outputFile = new File(outputFilename);
    Assert.assertTrue(outputFile.exists());
    Assert.assertTrue(outputFile.length() > 0);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

  @Test
  public void testExecutableForSchema()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<SchemaTextOptions> executable = (Executable<SchemaTextOptions>) appContext
      .getBean("executableForSchema");
    final DataSource dataSource = (DataSource) appContext.getBean("dataSource");

    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executable.execute(dataSource);

    final File outputFile = new File(outputFilename);
    Assert.assertTrue(outputFile.exists());
    Assert.assertTrue(outputFile.length() > 0);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

  @Test
  public void testExecutableForVelocity()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<SchemaTextOptions> executable = (Executable<SchemaTextOptions>) appContext
      .getBean("executableForVelocity");
    final DataSource dataSource = (DataSource) appContext.getBean("dataSource");

    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executable.execute(dataSource);

    final File outputFile = new File(outputFilename);
    Assert.assertTrue(outputFile.exists());
    Assert.assertTrue(outputFile.length() > 0);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
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

}
