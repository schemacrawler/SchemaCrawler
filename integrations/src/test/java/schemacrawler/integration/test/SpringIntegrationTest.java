package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Schema;
import schemacrawler.tools.Executable;
import schemacrawler.tools.datatext.DataTextFormatOptions;
import schemacrawler.tools.grep.GrepOptions;
import schemacrawler.tools.operation.OperationOptions;
import schemacrawler.tools.schematext.SchemaTextOptions;
import dbconnector.datasource.PropertiesDataSourceException;
import dbconnector.test.TestUtility;

public class SpringIntegrationTest
{

  private static TestUtility testUtility = new TestUtility();

  @AfterClass
  public static void afterAllTests()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    testUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  private ApplicationContext appContext;

  @Before
  public void setupDatabase()
  {
    appContext = new ClassPathXmlApplicationContext("context.xml");
  }

  @Test
  public void testExecutableForCount()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<OperationOptions> executable = (Executable<OperationOptions>) appContext
      .getBean("executableForCount");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @Test
  public void testExecutableForFreeMarker()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<SchemaTextOptions> executable = (Executable<SchemaTextOptions>) appContext
      .getBean("executableForFreeMarker");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @Test
  public void testExecutableForQuery()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<DataTextFormatOptions> executable = (Executable<DataTextFormatOptions>) appContext
      .getBean("executableForQuery");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @Test
  public void testExecutableForSchema()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<SchemaTextOptions> executable = (Executable<SchemaTextOptions>) appContext
      .getBean("executableForSchema");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @Test
  public void testExecutableForVelocity()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<SchemaTextOptions> executable = (Executable<SchemaTextOptions>) appContext
      .getBean("executableForVelocity");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @Test
  public void testExecutableForGrep()
    throws Exception
  {
    final String outputFilename = File.createTempFile("schemacrawler", "test")
      .getAbsolutePath();

    final Executable<GrepOptions> executable = (Executable<GrepOptions>) appContext
      .getBean("executableForGrep");
    executable.getToolOptions().getOutputOptions()
      .setOutputFileName(outputFilename);

    executeAndCheckForOutputFile(executable, outputFilename);
  }

  @Test
  public void testSchema()
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = (SchemaCrawlerOptions) appContext
      .getBean("schemaCrawlerOptions");
    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.maximum,
                                                  schemaCrawlerOptions);
    assertEquals(6, schema.getTables().length);
  }

  private void executeAndCheckForOutputFile(final Executable<?> executable,
                                            final String outputFilename)
    throws Exception
  {
    executable.execute(testUtility.getDataSource());

    final File outputFile = new File(outputFilename);
    Assert.assertTrue(outputFile.exists());
    Assert.assertTrue(outputFile.length() > 0);
    if (!outputFile.delete())
    {
      fail("Cannot delete output file");
    }
  }

}
