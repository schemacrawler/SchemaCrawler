/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.mysql;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import schemacrawler.plugins.dbconnectors.DatabaseConnectorDefinitionAdapter;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbconnectors.yaml.DatabasePluginYamlDeserializer;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public final class MySQLDatabaseConnector extends DatabaseConnector {

  private static final Predicate<String> urlMatcher =
      Pattern.compile("jdbc:(mysql|mariadb):.*").asMatchPredicate();

  private static DatabaseConnectorOptions databaseConnectorOptions() {
    final DatabaseConnectorDefinition definition =
        new DatabasePluginYamlDeserializer()
            .parse(new ClasspathInputResource("dbconnectors/mysql.yaml"));

    return new DatabaseConnectorDefinitionAdapter(definition)
        .toDatabaseConnectorOptionsBuilder()
        .withUrlSupportPredicate(url -> urlMatcher.test(url))
        .withSchemaRetrievalOptionsBuilder(
            (builder, conn) -> {
              builder.withEnumDataTypeHelper(new MySQLEnumDataTypeHelper());
            })
        .build();
  }

  public MySQLDatabaseConnector() {
    super(databaseConnectorOptions());
  }
}
