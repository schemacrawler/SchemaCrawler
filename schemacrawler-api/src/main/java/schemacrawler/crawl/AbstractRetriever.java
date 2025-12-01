/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import static schemacrawler.schema.DataTypeType.system;
import static schemacrawler.schema.DataTypeType.user_defined;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;
import static schemacrawler.utility.MetaDataUtility.inclusionRuleString;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.Retriever;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;

/** Base class for retriever that uses database metadata to get the details about the schema. */
@Retriever
abstract class AbstractRetriever {

  final MutableCatalog catalog;
  private final SchemaCrawlerOptions options;
  private final RetrieverConnection retrieverConnection;
  private final DataTypeLookup dataTypeLookup;

  AbstractRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options) {
    this.retrieverConnection =
        requireNonNull(retrieverConnection, "No retriever connection provided");
    this.catalog = catalog;
    this.options = requireNonNull(options, "No SchemaCrawler options provided");
    dataTypeLookup = new DataTypeLookup(retrieverConnection, catalog);
  }

  /**
   * Checks whether the provided database object belongs to the specified schema.
   *
   * @param dbObject Database object to check
   * @param catalogName Database catalog to check against
   * @param schemaName Database schema to check against
   * @return Whether the database object belongs to the specified schema
   */
  final boolean belongsToSchema(
      final DatabaseObject dbObject, final String catalogName, final String schemaName) {
    if (dbObject == null) {
      return false;
    }

    final boolean supportsCatalogs = retrieverConnection.isSupportsCatalogs();

    boolean belongsToCatalog = true;
    boolean belongsToSchema = true;
    if (supportsCatalogs) {
      final String dbObjectCatalogName = dbObject.getSchema().getCatalogName();
      if (catalogName != null && !catalogName.equals(dbObjectCatalogName)) {
        belongsToCatalog = false;
      }
    }
    final String dbObjectSchemaName = dbObject.getSchema().getName();
    if (schemaName != null && !schemaName.equals(dbObjectSchemaName)) {
      belongsToSchema = false;
    }
    return belongsToCatalog && belongsToSchema;
  }

  final NamedObjectList<SchemaReference> getAllSchemas() {
    return catalog.getAllSchemas();
  }

  final Map<String, String> getLimitMap() {
    final Map<String, String> limitMap = new HashMap<>();
    limitMap.put(
        "schema-inclusion-rule",
        inclusionRuleString(options.limitOptions().get(ruleForSchemaInclusion)));
    limitMap.put(
        "table-inclusion-rule",
        inclusionRuleString(options.limitOptions().get(ruleForTableInclusion)));
    return limitMap;
  }

  final Map<String, String> getLimitMap(final Schema schema) {
    final Map<String, String> limitMap = getLimitMap();
    if (schema != null) {
      limitMap.put("catalog-name", trimToEmpty(catalog.getName()));
      limitMap.put("schema-name", trimToEmpty(schema.getName()));
    }
    return limitMap;
  }

  final RetrieverConnection getRetrieverConnection() {
    return retrieverConnection;
  }

  final MutableColumnDataType lookupColumnDataType(
      final Schema schema, final String databaseSpecificTypeName, final int dataType) {
    return dataTypeLookup.lookupDataType(schema, databaseSpecificTypeName, dataType);
  }

  /**
   * Creates a data type from the JDBC data type id, and the database specific type name, if it does
   * not exist.
   *
   * @param databaseSpecificTypeName Database specific type name
   * @param javaSqlTypeInt JDBC data type
   * @return Column data type
   */
  final MutableColumnDataType lookupOrCreateSystemColumnDataType(
      final String databaseSpecificTypeName, final int javaSqlTypeInt) {
    return dataTypeLookup.lookupOrCreateDataType(
        new SchemaReference(), databaseSpecificTypeName, system, javaSqlTypeInt, null);
  }

  /**
   * Creates a data type from the JDBC data type id, and the database specific type name, if it does
   * not exist.
   *
   * @param schema Schema
   * @param databaseSpecificTypeName Database specific type name
   * @param javaSqlTypeInt JDBC data type
   * @return Column data type
   */
  final MutableColumnDataType lookupOrCreateUserDefinedColumnDataType(
      final Schema schema,
      final String databaseSpecificTypeName,
      final int javaSqlTypeInt,
      final String mappedClassName) {
    return dataTypeLookup.lookupOrCreateDataType(
        schema, databaseSpecificTypeName, user_defined, javaSqlTypeInt, mappedClassName);
  }

  final Optional<MutableRoutine> lookupRoutine(
      final String catalogName,
      final String schemaName,
      final String routineName,
      final String specificName) {
    return catalog.lookupRoutine(
        new NamedObjectKey(catalogName, schemaName, routineName, specificName));
  }

  final Optional<MutableTable> lookupTable(
      final String catalogName, final String schemaName, final String tableName) {
    return catalog.lookupTable(new NamedObjectKey(catalogName, schemaName, tableName));
  }

  final String normalizeCatalogName(final String name) {
    if (retrieverConnection.isSupportsCatalogs()) {
      return name;
    }
    return null;
  }

  final String normalizeSchemaName(final String name) {
    if (retrieverConnection.isSupportsSchemas()) {
      return name;
    }
    return null;
  }
}
