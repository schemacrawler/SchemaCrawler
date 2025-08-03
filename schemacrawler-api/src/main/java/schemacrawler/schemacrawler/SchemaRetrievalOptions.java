/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;
import java.sql.Connection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.schema.TableTypes;
import schemacrawler.utility.TypeMap;
import us.fatehi.utility.ObjectToString;
import us.fatehi.utility.datasource.DatabaseServerType;

/**
 * Provides for database specific overrides for SchemaCrawler functionality. This can add or inject
 * database plugins, or override defaults. It is recommended to build these options using factory
 * methods in SchemaCrawlerUtility.
 */
public final class SchemaRetrievalOptions implements Options {

  private final DatabaseServerType dbServerType;
  private final String identifierQuoteString;
  private final Identifiers identifiers;
  private final InformationSchemaViews informationSchemaViews;
  private final boolean supportsCatalogs;
  private final boolean supportsSchemas;
  private final TypeMap typeMap;
  private final TableTypes tableTypes;
  private final EnumDataTypeHelper enumDataTypeHelper;
  private final EnumMap<SchemaInfoMetadataRetrievalStrategy, MetadataRetrievalStrategy>
      metadataRetrievalStrategyMap;
  private final Consumer<Connection> connectionInitializer;

  protected SchemaRetrievalOptions(final SchemaRetrievalOptionsBuilder builder) {
    final SchemaRetrievalOptionsBuilder bldr =
        builder == null ? SchemaRetrievalOptionsBuilder.builder() : builder;

    dbServerType = bldr.dbServerType;
    supportsCatalogs = bldr.overridesSupportsCatalogs.orElse(bldr.supportsCatalogs);
    supportsSchemas = bldr.overridesSupportsSchemas.orElse(bldr.supportsSchemas);
    identifierQuoteString = bldr.identifierQuoteString;
    informationSchemaViews = bldr.informationSchemaViews;
    identifiers = bldr.identifiers;
    typeMap = bldr.overridesTypeMap.orElse(new TypeMap());
    tableTypes = bldr.tableTypes;
    enumDataTypeHelper = bldr.enumDataTypeHelper;
    metadataRetrievalStrategyMap = new EnumMap<>(bldr.metadataRetrievalStrategyMap);
    connectionInitializer = bldr.connectionInitializer;
  }

  public MetadataRetrievalStrategy get(
      final SchemaInfoMetadataRetrievalStrategy schemaInfoMetadataRetrievalStrategy) {
    requireNonNull(
        schemaInfoMetadataRetrievalStrategy, "No schema info metadata retrieval strategy provided");
    return metadataRetrievalStrategyMap.get(schemaInfoMetadataRetrievalStrategy);
  }

  public Consumer<Connection> getConnectionInitializer() {
    return connectionInitializer;
  }

  public DatabaseServerType getDatabaseServerType() {
    return dbServerType;
  }

  public EnumDataTypeHelper getEnumDataTypeHelper() {
    return enumDataTypeHelper;
  }

  public String getIdentifierQuoteString() {
    return identifierQuoteString;
  }

  public Identifiers getIdentifiers() {
    return identifiers;
  }

  public InformationSchemaViews getInformationSchemaViews() {
    return informationSchemaViews;
  }

  public TableTypes getTableTypes() {
    return tableTypes;
  }

  public TypeMap getTypeMap() {
    return typeMap;
  }

  public boolean isSupportsCatalogs() {
    return supportsCatalogs;
  }

  public boolean isSupportsSchemas() {
    return supportsSchemas;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }

  Map<SchemaInfoMetadataRetrievalStrategy, MetadataRetrievalStrategy>
      getMetadataRetrievalStrategyMap() {
    return new EnumMap<>(metadataRetrievalStrategyMap);
  }
}
