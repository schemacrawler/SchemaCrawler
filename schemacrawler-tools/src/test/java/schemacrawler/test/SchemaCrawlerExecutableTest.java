/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static java.lang.System.lineSeparator;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static us.fatehi.test.utility.TestUtility.readFileFully;
import static us.fatehi.utility.IOUtility.createTempFilePath;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.test.utility.ExecutableTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class SchemaCrawlerExecutableTest {

  @Test
  public void executable(final DatabaseConnectionSource dataSource) throws Exception {

    final Path testOutputFile = createTempFilePath("sc", "data");

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final OutputOptions outputOptions =
        ExecutableTestUtility.newOutputOptions("text", testOutputFile);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("test-command");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);
    executable.setDataSource(dataSource);
    executable.execute();

    assertThat(
        "Output generated from schemacrawler.test.utility.testcommand.TestCommand"
            + lineSeparator()
            + "TestOptions [testCommandParameter=]"
            + lineSeparator(),
        equalTo(readFileFully(testOutputFile)));
    assertThat(executable.toString(), is("test-command"));

    assertThat(executable.getOutputOptions(), is(outputOptions));
    assertThat(executable.getSchemaCrawlerOptions(), is(schemaCrawlerOptions));

    final Catalog catalog = executable.getCatalog();
    assertThat(catalog.getSchemas(), hasSize(6));
    assertThat(catalog.getTables(), hasSize(20));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void executable_bad_command(final DatabaseConnectionSource dataSource) throws Exception {

    final String command1 = "bad-command";
    final SchemaCrawlerExecutable executable1 = new SchemaCrawlerExecutable(command1);
    executable1.setDataSource(dataSource);
    final ExecutionRuntimeException ex1 =
        assertThrows(ExecutionRuntimeException.class, () -> executable1.execute());
    assertThat(ex1.getMessage(), is("Unknown command <" + command1 + ">"));

    final String command2 = "test-command";
    final SchemaCrawlerExecutable executable2 = new SchemaCrawlerExecutable(command2);
    executable2.setDataSource(dataSource);
    final Config config = new Config();
    config.put("return-null", "true");
    executable2.setAdditionalConfiguration(config);
    final InternalRuntimeException ex2 =
        assertThrows(InternalRuntimeException.class, () -> executable2.execute());
    assertThat(ex2.getMessage(), is("Cannot run command <" + command2 + ">"));
  }

  @Test
  public void executable_options() throws Exception {

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("test-command");

    // SchemaCrawler options
    assertThat(executable.getSchemaCrawlerOptions(), is(not(nullValue())));

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThat(executable.getSchemaCrawlerOptions(), is(sameInstance(schemaCrawlerOptions)));

    executable.setSchemaCrawlerOptions(null);
    assertThat(executable.getSchemaCrawlerOptions(), is(not(nullValue())));
    assertThat(executable.getSchemaCrawlerOptions(), is(not(sameInstance(schemaCrawlerOptions))));

    // // Output options
    assertThat(executable.getOutputOptions(), is(not(nullValue())));

    final OutputOptions outputOptions = OutputOptionsBuilder.newOutputOptions();
    executable.setOutputOptions(outputOptions);
    assertThat(executable.getOutputOptions(), is(sameInstance(outputOptions)));

    executable.setOutputOptions(null);
    assertThat(executable.getOutputOptions(), is(not(nullValue())));
    assertThat(executable.getOutputOptions(), is(not(sameInstance(outputOptions))));

    // Schema retrieval options
    assertThat(executable.getSchemaRetrievalOptions(), is(nullValue()));

    final SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();
    executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
    assertThat(executable.getSchemaRetrievalOptions(), is(sameInstance(schemaRetrievalOptions)));

    executable.setSchemaRetrievalOptions(null);
    assertThat(executable.getSchemaRetrievalOptions(), is(nullValue()));
    assertThat(
        executable.getSchemaRetrievalOptions(), is(not(sameInstance(schemaRetrievalOptions))));

    // Catalog
    assertThat(executable.getCatalog(), is(nullValue()));

    final Catalog catalog = mock(Catalog.class);
    executable.setCatalog(catalog);
    assertThat(executable.getCatalog(), is(sameInstance(catalog)));

    executable.setCatalog(null);
    assertThat(executable.getCatalog(), is(nullValue()));
    assertThat(executable.getCatalog(), is(not(sameInstance(catalog))));
  }

  @Test
  public void executable_with_settings(final DatabaseConnectionSource dataSource) throws Exception {

    final Path testOutputFile = createTempFilePath("sc", "data");
    final Catalog mockCatalog = mock(Catalog.class);
    final SchemaRetrievalOptions mockSchemaRetrievalOptions =
        SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();
    final Config config = new Config();
    config.put("uses-connection", "false");

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("test-command");

    final OutputOptions outputOptions =
        ExecutableTestUtility.newOutputOptions("text", testOutputFile);

    executable.setOutputOptions(outputOptions);
    executable.setDataSource(dataSource);
    executable.setAdditionalConfiguration(config);
    executable.setCatalog(mockCatalog);
    executable.setSchemaRetrievalOptions(mockSchemaRetrievalOptions);
    executable.execute();

    assertThat(
        "Output generated from schemacrawler.test.utility.testcommand.TestCommand"
            + lineSeparator()
            + "TestOptions [testCommandParameter=]"
            + lineSeparator(),
        equalTo(readFileFully(testOutputFile)));
    assertThat(executable.toString(), is("test-command"));

    assertThat(executable.getCatalog(), is(sameInstance(mockCatalog)));
    assertThat(
        executable.getSchemaRetrievalOptions(), is(sameInstance(mockSchemaRetrievalOptions)));
  }
}
