/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.utility;

import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.options.Config;

public final class SchemaRetrievalOptionsConfig {

  /**
   * Information schema views from a map.
   *
   * @param informationSchemaViewsSql Map of information schema view definitions.
   */
  public static InformationSchemaViewsBuilder fromConfig(
      final InformationSchemaViewsBuilder providedBuilder, final Config informationSchemaViewsSql) {
    final InformationSchemaViewsBuilder builder;
    if (providedBuilder == null) {
      builder = InformationSchemaViewsBuilder.builder();
    } else {
      builder = providedBuilder;
    }

    if (informationSchemaViewsSql == null) {
      return builder;
    }

    for (final InformationSchemaKey informationSchemaKey : InformationSchemaKey.values()) {
      final String informationSchemaKeyConfigKey =
          String.format("select.%s.%s", informationSchemaKey.getType(), informationSchemaKey);
      if (informationSchemaViewsSql.containsKey(informationSchemaKeyConfigKey)) {
        try {
          builder.withSql(
              informationSchemaKey,
              informationSchemaViewsSql.getStringValue(informationSchemaKeyConfigKey, ""));
        } catch (final IllegalArgumentException e) {
          // Ignore
        }
      }
    }

    return builder;
  }

  public static SchemaRetrievalOptionsBuilder fromConfig(
      final SchemaRetrievalOptionsBuilder providedBuilder, final Config config) {
    final SchemaRetrievalOptionsBuilder builder;
    if (providedBuilder == null) {
      builder = SchemaRetrievalOptionsBuilder.builder();
    } else {
      builder = providedBuilder;
    }

    final Config configProperties;
    if (config == null) {
      configProperties = new Config();
    } else {
      configProperties = new Config(config);
    }

    final InformationSchemaViewsBuilder informationSchemaViewsBuilder =
        InformationSchemaViewsBuilder.builder(builder.getInformationSchemaViews());
    SchemaRetrievalOptionsConfig.fromConfig(informationSchemaViewsBuilder, configProperties);
    builder.withInformationSchemaViews(informationSchemaViewsBuilder.toOptions());

    for (final SchemaInfoMetadataRetrievalStrategy metadataRetrievalStrategy :
        SchemaInfoMetadataRetrievalStrategy.values()) {
      final MetadataRetrievalStrategy currentValue = builder.get(metadataRetrievalStrategy);
      final String configKey =
          "schemacrawler.schema.retrieval.strategy." + metadataRetrievalStrategy.getKey();
      final MetadataRetrievalStrategy configValue =
          configProperties.getEnumValue(configKey, currentValue);
      builder.with(metadataRetrievalStrategy, configValue);
    }

    return builder;
  }
}
