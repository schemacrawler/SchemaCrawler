/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;

@WithTestDatabase
@ResolveTestContext
public class CatalogAttributesCommandLineTest {

  private static final String CATALOG_ATTRIBUTES_OUTPUT = "catalog_attributes_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(CATALOG_ATTRIBUTES_OUTPUT);
  }

  @Test
  public void showRemarks(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {

    final Path attributesFile =
        copyResourceToTempFile("/" + CATALOG_ATTRIBUTES_OUTPUT + "attributes.yaml");

    final String referenceFile = testContext.testMethodName() + ".txt";

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("--no-info", "true");
    argsMap.put("--attributes-file", attributesFile.toString());

    assertThat(
        outputOf(
            commandlineExecution(
                connectionInfo,
                SchemaTextDetailType.schema.name(),
                argsMap,
                TextOutputFormat.text)),
        hasSameContentAs(classpathResource(CATALOG_ATTRIBUTES_OUTPUT + referenceFile)));
  }
}
