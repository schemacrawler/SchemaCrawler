package schemacrawler.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

public class SpinThroughTest
  extends BaseDatabaseTest
{

  @BeforeClass
  public static void clean()
    throws IOException
  {
    FileUtils.deleteDirectory(new File("./target/unit_tests_results_output",
                                       SPIN_THROUGH_OUTPUT));
  }

  private static final String SPIN_THROUGH_OUTPUT = "spin_through_output/";
  private static final String[] outputFormats = new String[] {
      OutputFormat.text.name(),
      OutputFormat.html.name(),
      OutputFormat.json.name(),
      GraphOutputFormat.scdot.name()
  };
  private File hsqldbProperties;

  @Before
  public void copyResources()
    throws IOException
  {
    hsqldbProperties = copyResourceToTempFile("/hsqldb.INFORMATION_SCHEMA.config.properties");
  }

  @Test
  public void spinThroughExecutable()
    throws Exception
  {

    final List<String> failures = new ArrayList<>();
    for (final InfoLevel infoLevel: InfoLevel.values())
    {
      if (infoLevel == InfoLevel.unknown)
      {
        continue;
      }
      for (final String outputFormat: outputFormats)
      {
        for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
          .values())
        {
          final String referenceFile = referenceFile(schemaTextDetailType,
                                                     infoLevel,
                                                     outputFormat);
          final File testOutputFile = createTempFile(schemaTextDetailType,
                                                     infoLevel,
                                                     outputFormat);

          final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                                testOutputFile);

          final Config config = Config.load(hsqldbProperties.getAbsolutePath());
          final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
          schemaCrawlerOptions.setSchemaInfoLevel(infoLevel
            .getSchemaInfoLevel());
          schemaCrawlerOptions.setSequenceInclusionRule(new IncludeAll());
          schemaCrawlerOptions.setSynonymInclusionRule(new IncludeAll());

          final Executable executable = new SchemaCrawlerExecutable(schemaTextDetailType
            .name());
          executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
          executable.setOutputOptions(outputOptions);
          executable.execute(getConnection());

          failures.addAll(compareOutput(SPIN_THROUGH_OUTPUT + referenceFile,
                                        testOutputFile,
                                        outputFormat));
        }
      }
    }

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void spinThroughMain()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();

    for (final InfoLevel infoLevel: InfoLevel.values())
    {
      if (infoLevel == InfoLevel.unknown)
      {
        continue;
      }

      for (final String outputFormat: outputFormats)
      {
        for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
          .values())
        {
          final String referenceFile = referenceFile(schemaTextDetailType,
                                                     infoLevel,
                                                     outputFormat);
          final File testOutputFile = createTempFile(schemaTextDetailType,
                                                     infoLevel,
                                                     outputFormat);

          Main.main(new String[] {
              "-driver=org.hsqldb.jdbc.JDBCDriver",
              "-url=jdbc:hsqldb:hsql://localhost/schemacrawler",
              "-user=sa",
              "-password=",
              "-g=" + hsqldbProperties.getAbsolutePath(),
              "-sequences=.*",
              "-synonyms=.*",
              "-infolevel=" + infoLevel,
              "-command=" + schemaTextDetailType,
              "-outputformat=" + outputFormat,
              "-outputfile=" + testOutputFile.getAbsolutePath(),
          });

          failures.addAll(compareOutput(SPIN_THROUGH_OUTPUT + referenceFile,
                                        testOutputFile,
                                        outputFormat));
        }
      }
    }
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  private File createTempFile(final SchemaTextDetailType schemaTextDetailType,
                              final InfoLevel infoLevel,
                              final String outputFormat)
  {
    return org.apache.tools.ant.util.FileUtils.getFileUtils()
      .createTempFile(String.format("%s.%s", schemaTextDetailType, infoLevel),
                      outputFormat,
                      null,
                      true,
                      false);
  }

  private String referenceFile(final SchemaTextDetailType schemaTextDetailType,
                               final InfoLevel infoLevel,
                               final String outputFormat)
  {
    final String referenceFile = String.format("%d%d.%s_%s.%s",
                                               schemaTextDetailType.ordinal(),
                                               infoLevel.ordinal(),
                                               schemaTextDetailType,
                                               infoLevel,
                                               outputFormat);
    return referenceFile;
  }

}
