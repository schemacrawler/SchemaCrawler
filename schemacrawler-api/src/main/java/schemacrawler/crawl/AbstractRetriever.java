/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import static schemacrawler.schema.DataTypeType.user_defined;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;
import static schemacrawler.utility.MetaDataUtility.inclusionRuleString;
import static us.fatehi.utility.CollectionsUtility.splitList;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.DataTypeType;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.Retriever;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.utility.TypeMap;

/** Base class for retriever that uses database metadata to get the details about the schema. */
@Retriever
abstract class AbstractRetriever {

  record DataTypeLookupCandidate(Schema schema, String dataTypeName) {
    boolean hasSchema() {
      return schema != null;
    }
  }

  private static final Logger LOGGER = Logger.getLogger(AbstractRetriever.class.getName());
  final MutableCatalog catalog;
  private final SchemaCrawlerOptions options;

  private final RetrieverConnection retrieverConnection;

  AbstractRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options) {
    this.retrieverConnection =
        requireNonNull(retrieverConnection, "No retriever connection provided");
    this.catalog = catalog;
    this.options = requireNonNull(options, "No SchemaCrawler options provided");
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

  final void logPossiblyUnsupportedSQLFeature(
      final Supplier<String> message, final SQLException e) {
    // HYC00 = Optional feature not implemented
    // HY000 = General error
    // (HY000 is thrown by the Teradata JDBC driver for unsupported
    // functions)
    if ("HYC00".equalsIgnoreCase(e.getSQLState())
        || "HY000".equalsIgnoreCase(e.getSQLState())
        || "0A000".equalsIgnoreCase(e.getSQLState())
        || e instanceof SQLFeatureNotSupportedException) {
      logSQLFeatureNotSupported(message, e);
    } else {
      LOGGER.log(Level.WARNING, e, message);
    }
  }

  final void logSQLFeatureNotSupported(final Supplier<String> message, final Throwable e) {
    LOGGER.log(Level.WARNING, message);
    LOGGER.log(Level.FINE, e, message);
  }

  final MutableColumnDataType lookupColumnDataType(
      final Schema schema, final String databaseSpecificTypeName, final int dataType) {

    final DataTypeLookupCandidate dataTypeLookupCandidate =
        parseDataTypeName(databaseSpecificTypeName);

    final Schema lookupSchema = dataTypeLookupCandidate.schema();
    final String lookupTypeName = dataTypeLookupCandidate.dataTypeName();

    // Construct a "match" for the column data type, even if it is
    // not available in the catalog
    MutableColumnDataType columnDataType = null;
    // 1. If lookup schema was specified, use that
    if (lookupSchema != null) {
      final Optional<MutableColumnDataType> lookupColumnDataType =
          catalog.lookupColumnDataType(lookupSchema, lookupTypeName);
      if (lookupColumnDataType.isPresent()) {
        columnDataType = lookupColumnDataType.get();
      }
    }
    // 2. Lookup as a system data-type
    if (columnDataType == null) {
      final Optional<MutableColumnDataType> lookupSystemColumnDataType =
          catalog.lookupSystemColumnDataType(lookupTypeName);
      if (lookupSystemColumnDataType.isPresent()) {
        columnDataType = lookupSystemColumnDataType.get();
      }
    }
    // 3. Lookup as a user-defined data-type
    if (columnDataType == null) {
      final Optional<MutableColumnDataType> lookupColumnDataType =
          catalog.lookupColumnDataType(schema, lookupTypeName);
      if (lookupColumnDataType.isPresent()) {
        columnDataType = lookupColumnDataType.get();
      }
    }
    // 4. Fallback
    if (columnDataType == null) {
      columnDataType = lookupOrCreateColumnDataType(schema, lookupTypeName, user_defined, dataType);
      catalog.addColumnDataType(columnDataType);
    }

    return columnDataType;
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
  final MutableColumnDataType lookupOrCreateColumnDataType(
      final Schema schema,
      final String databaseSpecificTypeName,
      final DataTypeType type,
      final int javaSqlTypeInt) {
    return lookupOrCreateColumnDataType(
        schema, databaseSpecificTypeName, type, javaSqlTypeInt, null);
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
  final MutableColumnDataType lookupOrCreateColumnDataType(
      final Schema schema,
      final String databaseSpecificTypeName,
      final DataTypeType type,
      final int javaSqlTypeInt,
      final String mappedClassName) {
    final MutableColumnDataType columnDataType =
        lookupColumnDataTypeForCreate(schema, databaseSpecificTypeName, type);
    // If new data type, fill the fields
    final boolean isNewColumnDataType =
        catalog
            .lookupColumnDataType(columnDataType.getSchema(), columnDataType.getName())
            .isEmpty();
    if (isNewColumnDataType) {
      final JavaSqlType javaSqlType = retrieverConnection.getJavaSqlTypes().valueOf(javaSqlTypeInt);
      columnDataType.setJavaSqlType(javaSqlType);
      if (isBlank(mappedClassName)) {
        final TypeMap typeMap = retrieverConnection.getTypeMap();
        final Class<?> mappedClass;
        if (typeMap.containsKey(databaseSpecificTypeName)) {
          mappedClass = typeMap.get(databaseSpecificTypeName);
        } else {
          mappedClass = typeMap.get(javaSqlType.getName());
        }
        columnDataType.setTypeMappedClass(mappedClass);
      } else {
        columnDataType.setTypeMappedClass(mappedClassName);
      }
      columnDataType.withQuoting(getRetrieverConnection().getIdentifiers());

      // NOTE: Do not add the data type to the catalog
    }
    return columnDataType;
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

  /**
   * Looks up the column data type in the catalog. If it is in the catalog, that type is returned.
   * If it is not in the catalog, a column data type is returned nevertheless based on the input
   * schema and name.
   *
   * @param schema Schema to search in, which may be overridden if the type name includes the schema
   * @param databaseSpecificTypeName Name to look up, after parsing out the schema
   * @param type System or user defined
   * @return Column data type
   */
  private MutableColumnDataType lookupColumnDataTypeForCreate(
      final Schema schema, final String databaseSpecificTypeName, final DataTypeType type) {

    final DataTypeLookupCandidate dataTypeLookupCandidate =
        parseDataTypeName(databaseSpecificTypeName);

    final Schema lookupSchema =
        dataTypeLookupCandidate.hasSchema() ? dataTypeLookupCandidate.schema() : schema;
    final String lookupTypeName = dataTypeLookupCandidate.dataTypeName();

    if (isBlank(lookupTypeName)) {
      return new MutableColumnDataType(schema, lookupTypeName, type);
    }
    // Construct a "match" for the column data type, even if it is
    // not available in the catalog
    final MutableColumnDataType columnDataType =
        catalog
            .lookupColumnDataType(lookupSchema, lookupTypeName)
            .orElse(new MutableColumnDataType(schema, lookupTypeName, type));
    return columnDataType;
  }

  private DataTypeLookupCandidate parseDataTypeName(final String databaseSpecificTypeName) {

    // Default schema to use if schema is not found
    final SchemaReference unspecifiedSchema = null;

    if (isBlank(databaseSpecificTypeName)) {
      return new DataTypeLookupCandidate(unspecifiedSchema, "");
    }
    if (!databaseSpecificTypeName.contains(".")) {
      return new DataTypeLookupCandidate(unspecifiedSchema, databaseSpecificTypeName);
    }

    // PostgreSQL and IBM DB2 may quote column data type names, so "unquote" them
    final Identifiers identifiers = getRetrieverConnection().getIdentifiers();
    final String[] splitName = splitList(databaseSpecificTypeName, "\\.");
    if (splitName.length == 0) {
      return new DataTypeLookupCandidate(unspecifiedSchema, databaseSpecificTypeName);
    }
    for (int i = 0; i < splitName.length; i++) {
      splitName[i] = identifiers.unquoteName(splitName[i]);
    }

    // Create lookup schema and lookup name
    final Schema lookupSchema =
        switch (splitName.length) {
          default -> unspecifiedSchema;
          case 2 ->
              catalog.getSchemas().stream()
                  .filter(dbSchema -> dbSchema.getFullName().endsWith(splitName[0]))
                  .findFirst()
                  .orElse(unspecifiedSchema);
          case 3 -> {
            final String schemaName = new SchemaReference(splitName[0], splitName[1]).getFullName();
            yield catalog.lookupSchema(schemaName).orElse(unspecifiedSchema);
          }
        };
    final String lookupTypeName = splitName[splitName.length - 1];

    return new DataTypeLookupCandidate(lookupSchema, lookupTypeName);
  }
}
