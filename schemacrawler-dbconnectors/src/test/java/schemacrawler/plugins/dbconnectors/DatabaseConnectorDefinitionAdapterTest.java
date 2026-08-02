/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbconnectors.yaml.DatabasePluginYamlDeserializer;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class DatabaseConnectorDefinitionAdapterTest {

  private final DatabasePluginYamlDeserializer deserializer = new DatabasePluginYamlDeserializer();

  @Test
  public void toOptionsBuilderMissingInformationSchemaFolder() {
    final DatabaseConnectorDefinition definition =
        deserializer.parse(new ClasspathInputResource("schemacrawler-dbconnectors/h2.yaml"));

    final DatabaseConnectorOptions options =
        new DatabaseConnectorDefinitionAdapter(definition).toDatabaseConnectorOptions();
    final InformationSchemaViewsBuilder viewsBuilder = InformationSchemaViewsBuilder.builder();
    options.informationSchemaViewsBuildProcess().accept(viewsBuilder, null);
    final InformationSchemaViews views = viewsBuilder.toOptions();

    assertThat(views.size(), is(0));
  }

  @Test
  public void toOptionsBuilderWithInformationSchemaFolder() {
    final DatabaseConnectorDefinition definition =
        deserializer.parse(
            new ClasspathInputResource("schemacrawler-dbconnectors/adapter-test.yaml"));

    final DatabaseConnectorOptions options =
        new DatabaseConnectorDefinitionAdapter(definition).toDatabaseConnectorOptions();
    final InformationSchemaViewsBuilder viewsBuilder = InformationSchemaViewsBuilder.builder();
    options.informationSchemaViewsBuildProcess().accept(viewsBuilder, null);
    final InformationSchemaViews views = viewsBuilder.toOptions();

    assertThat(views.size(), is(1));
    assertThat(views.hasQuery(InformationSchemaKey.SERVER_INFORMATION), is(true));
  }
}
