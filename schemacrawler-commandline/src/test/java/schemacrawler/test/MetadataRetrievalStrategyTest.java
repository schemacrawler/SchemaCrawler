/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.test;


import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;
import static schemacrawler.test.utility.TestUtility.writeConfigToTempFile;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.crawl.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import sf.util.IOUtility;

public class MetadataRetrievalStrategyTest
  extends BaseDatabaseTest
{

  private static final String METADATA_RETRIEVAL_STRATEGY_OUTPUT = "metadata_retrieval_strategy_output/";

  @Rule
  public TestName testName = new TestName();

  private TestOutputStream out;
  private TestOutputStream err;

  @After
  public void cleanUpStreams()
  {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  public void overrideMetadataRetrievalStrategy()
    throws Exception
  {
    clean(METADATA_RETRIEVAL_STRATEGY_OUTPUT);

    new ArrayList<>();

    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.schema;
    final InfoLevel infoLevel = InfoLevel.minimum;

    final Config config = new Config();
    config.put("schemacrawler.schema.retrieval.strategy.tables",
               MetadataRetrievalStrategy.data_dictionary_all.name());
    final Path configFile = writeConfigToTempFile(config);

    final String referenceFile = testName.currentMethodName() + ".txt";
    final Path testOutputFile = IOUtility.createTempFilePath(referenceFile,
                                                             "data");

    final OutputFormat outputFormat = TextOutputFormat.text;

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
    argsMap.put("user", "sa");
    argsMap.put("password", "");
    argsMap.put("g", configFile.toString());
    argsMap.put("infolevel", infoLevel.name());
    argsMap.put("command", schemaTextDetailType.name());
    argsMap.put("outputformat", outputFormat.getFormat());
    argsMap.put("outputfile", testOutputFile.toString());
    argsMap.put("noinfo", "true");
    // argsMap.put("loglevel", Level.SEVERE.getName());

    Main.main(flattenCommandlineArgs(argsMap));

    // Check that System.err has an error
    out.assertEmpty();
    final String errorLog = err.getLog();
    assertThat(errorLog, containsString("No tables SQL provided"));

  }

  @Before
  public void setUpStreams()
    throws Exception
  {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

}
