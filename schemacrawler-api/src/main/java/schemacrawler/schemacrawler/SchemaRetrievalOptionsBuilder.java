/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static schemacrawler.plugin.EnumDataTypeHelper.NO_OP_ENUM_DATA_TYPE_HELPER;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.metadata;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.schema.TableTypes;
import schemacrawler.utility.TypeMap;
import us.fatehi.utility.datasource.DatabaseServerType;

public final class SchemaRetrievalOptionsBuilder
    implements OptionsBuilder<SchemaRetrievalOptionsBuilder, SchemaRetrievalOptions> {

  public static SchemaRetrievalOptionsBuilder builder() {
    return new SchemaRetrievalOptionsBuilder();
  }

  public static SchemaRetrievalOptionsBuilder builder(final SchemaRetrievalOptions options) {
    return new SchemaRetrievalOptionsBuilder().fromOptions(options);
  }

  public static SchemaRetrievalOptions newSchemaRetrievalOptions() {
    return new SchemaRetrievalOptionsBuilder().toOptions();
  }

  DatabaseServerType dbServerType;
  String identifierQuoteString;
  Identifiers identifiers;
  InformationSchemaViews informationSchemaViews;
  Optional<Boolean> overridesSupportsSchemas;
  Optional<Boolean> overridesSupportsCatalogs;
  Optional<TypeMap> overridesTypeMap;
  TableTypes tableTypes;
  boolean supportsCatalogs;
  boolean supportsSchemas;
  EnumDataTypeHelper enumDataTypeHelper;
  Map<SchemaInfoMetadataRetrievalStrategy, MetadataRetrievalStrategy> metadataRetrievalStrategyMap;
  Consumer<Connection> connectionInitializer;

  private SchemaRetrievalOptionsBuilder() {
    dbServerType = DatabaseServerType.UNKNOWN;
    informationSchemaViews = InformationSchemaViewsBuilder.newInformationSchemaViews();
    overridesSupportsSchemas = Optional.empty();
    overridesSupportsCatalogs = Optional.empty();
    supportsCatalogs = true;
    supportsSchemas = true;
    identifierQuoteString = "";
    identifiers = Identifiers.STANDARD;
    overridesTypeMap = Optional.empty();
    tableTypes =
        TableTypes.from("TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY");
    enumDataTypeHelper = NO_OP_ENUM_DATA_TYPE_HELPER;
    connectionInitializer = connection -> {};

    metadataRetrievalStrategyMap = new EnumMap<>(SchemaInfoMetadataRetrievalStrategy.class);
    for (final SchemaInfoMetadataRetrievalStrategy key :
        SchemaInfoMetadataRetrievalStrategy.values()) {
      metadataRetrievalStrategyMap.put(key, metadata);
    }
  }

  public SchemaRetrievalOptionsBuilder fromConnnection(final Connection connection) {
    if (connection == null) {
      return this;
    }

    DatabaseMetaData metaData;
    try {
      metaData = connection.getMetaData();
    } catch (final SQLException e) {
      // Ignore
      metaData = null;
    }

    identifierQuoteString = lookupIdentifierQuoteString(metaData);
    identifiers =
        IdentifiersBuilder.builder()
            .fromConnection(connection)
            .withIdentifierQuoteString(identifierQuoteString)
            .toOptions();

    supportsCatalogs = lookupSupportsCatalogs(metaData);
    supportsSchemas = lookupSupportsSchemas(metaData);

    if (overridesTypeMap.isEmpty()) {
      overridesTypeMap = Optional.of(new TypeMap(connection));
    }

    tableTypes = TableTypes.from(connection);

    return this;
  }

  @Override
  public SchemaRetrievalOptionsBuilder fromOptions(final SchemaRetrievalOptions options) {
    if (options == null) {
      return this;
    }

    dbServerType = options.getDatabaseServerType();
    informationSchemaViews = options.getInformationSchemaViews();
    overridesSupportsSchemas = Optional.empty();
    overridesSupportsCatalogs = Optional.empty();
    supportsCatalogs = options.isSupportsCatalogs();
    supportsSchemas = options.isSupportsSchemas();
    identifierQuoteString = options.getIdentifierQuoteString();
    identifiers = options.getIdentifiers();
    overridesTypeMap = Optional.empty();
    metadataRetrievalStrategyMap = options.getMetadataRetrievalStrategyMap();
    connectionInitializer = options.getConnectionInitializer();

    return this;
  }

  public MetadataRetrievalStrategy get(
      final SchemaInfoMetadataRetrievalStrategy schemaInfoMetadataRetrievalStrategy) {
    if (schemaInfoMetadataRetrievalStrategy != null) {
      return metadataRetrievalStrategyMap.get(schemaInfoMetadataRetrievalStrategy);
    }
    return null;
  }

  public InformationSchemaViews getInformationSchemaViews() {
    return informationSchemaViews;
  }

  @Override
  public SchemaRetrievalOptions toOptions() {
    return new SchemaRetrievalOptions(this);
  }

  public SchemaRetrievalOptionsBuilder with(
      final SchemaInfoMetadataRetrievalStrategy schemaInfoMetadataRetrievalStrategy,
      final MetadataRetrievalStrategy metadataRetrievalStrategy) {

    if (schemaInfoMetadataRetrievalStrategy == null) {
      return this;
    }

    if (metadataRetrievalStrategy == null) {
      // Reset to default
      metadataRetrievalStrategyMap.put(schemaInfoMetadataRetrievalStrategy, metadata);
    } else {
      metadataRetrievalStrategyMap.put(
          schemaInfoMetadataRetrievalStrategy, metadataRetrievalStrategy);
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withConnectionInitializer(
      final Consumer<Connection> connectionInitializer) {
    if (connectionInitializer == null) {
      this.connectionInitializer = connection -> {};
    } else {
      this.connectionInitializer = connectionInitializer;
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withDatabaseServerType(
      final DatabaseServerType dbServerType) {
    if (dbServerType == null) {
      this.dbServerType = DatabaseServerType.UNKNOWN;
    } else {
      this.dbServerType = dbServerType;
    }
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the database supports catalogs.
   */
  public SchemaRetrievalOptionsBuilder withDoesNotSupportCatalogs() {
    overridesSupportsCatalogs = Optional.of(false);
    return this;
  }

  /** Overrides the JDBC driver provided information about whether the database supports schema. */
  public SchemaRetrievalOptionsBuilder withDoesNotSupportSchemas() {
    overridesSupportsSchemas = Optional.of(false);
    return this;
  }

  public SchemaRetrievalOptionsBuilder withEnumDataTypeHelper(
      final EnumDataTypeHelper enumDataTypeHelper) {
    if (enumDataTypeHelper != null) {
      this.enumDataTypeHelper = enumDataTypeHelper;
    } else {
      this.enumDataTypeHelper = NO_OP_ENUM_DATA_TYPE_HELPER;
    }

    return this;
  }

  /**
   * Overrides the JDBC driver provided information about the identifier quote string.
   *
   * @param identifierQuoteString Value for the override
   */
  public SchemaRetrievalOptionsBuilder withIdentifierQuoteString(
      final String identifierQuoteString) {
    if (isBlank(identifierQuoteString)) {
      this.identifierQuoteString = "";
    } else {
      this.identifierQuoteString = identifierQuoteString;
    }
    return this;
  }

  public SchemaRetrievalOptionsBuilder withInformationSchemaViews(
      final InformationSchemaViews informationSchemaViews) {
    this.informationSchemaViews =
        InformationSchemaViewsBuilder.builder().fromOptions(informationSchemaViews).toOptions();
    return this;
  }

  public SchemaRetrievalOptionsBuilder withoutIdentifierQuoteString() {
    identifierQuoteString = "";
    return this;
  }

  public SchemaRetrievalOptionsBuilder withoutSupportsCatalogs() {
    overridesSupportsCatalogs = Optional.empty();
    return this;
  }

  public SchemaRetrievalOptionsBuilder withoutSupportsSchemas() {
    overridesSupportsSchemas = Optional.empty();
    return this;
  }

  /**
   * Overrides the JDBC driver provided information about whether the database supports catalogs.
   */
  public SchemaRetrievalOptionsBuilder withSupportsCatalogs() {
    overridesSupportsCatalogs = Optional.of(true);
    return this;
  }

  /** Overrides the JDBC driver provided information about whether the database supports schema. */
  public SchemaRetrievalOptionsBuilder withSupportsSchemas() {
    overridesSupportsSchemas = Optional.of(true);
    return this;
  }

  public SchemaRetrievalOptionsBuilder withTypeMap(final Map<String, Class<?>> typeMap) {
    if (typeMap == null) {
      overridesTypeMap = Optional.empty();
    } else {
      overridesTypeMap = Optional.of(new TypeMap(typeMap));
    }
    return this;
  }

  private String lookupIdentifierQuoteString(final DatabaseMetaData metaData) {
    // Default to SQL standard default
    String identifierQuoteString = "\"";

    if (!isBlank(this.identifierQuoteString)) {
      identifierQuoteString = this.identifierQuoteString;
    } else if (metaData != null) {
      try {
        identifierQuoteString = metaData.getIdentifierQuoteString();
      } catch (final SQLException e) {
        // Ignore
      }
    }

    return trimToEmpty(identifierQuoteString);
  }

  private boolean lookupSupportsCatalogs(final DatabaseMetaData metaData) {
    boolean supportsCatalogs = true;
    if (overridesSupportsCatalogs.isPresent()) {
      supportsCatalogs = overridesSupportsCatalogs.get();
    } else if (metaData != null) {
      try {
        supportsCatalogs = metaData.supportsCatalogsInTableDefinitions();
      } catch (final SQLException e) {
        // Ignore
      }
    }
    return supportsCatalogs;
  }

  private boolean lookupSupportsSchemas(final DatabaseMetaData metaData) {
    boolean supportsSchemas = true;
    if (overridesSupportsSchemas.isPresent()) {
      supportsSchemas = overridesSupportsSchemas.get();
    } else if (metaData != null) {
      try {
        supportsSchemas = metaData.supportsSchemasInTableDefinitions();
      } catch (final SQLException e) {
        // Ignore
      }
    }

    return supportsSchemas;
  }
}
