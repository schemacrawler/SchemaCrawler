/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import static schemacrawler.schema.DataTypeType.user_defined;
import static us.fatehi.utility.CollectionsUtility.splitList;
import static us.fatehi.utility.Utility.isBlank;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.DataTypeType;
import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.Retriever;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.utility.TypeMap;
import us.fatehi.utility.string.StringFormat;

/** Base class for retriever that uses database metadata to get the details about the schema. */
@Retriever
final class DataTypeLookup {

  private record SimpleDataTypeName(Schema schema, String dataTypeName) {
    boolean hasSchema() {
      return schema != null;
    }
  }

  private static final Logger LOGGER = Logger.getLogger(DataTypeLookup.class.getName());

  private final MutableCatalog catalog;
  private final RetrieverConnection retrieverConnection;
  private final Map<String, SimpleDataTypeName> parsedDataTypeNames;

  DataTypeLookup(final RetrieverConnection retrieverConnection, final MutableCatalog catalog) {
    this.retrieverConnection =
        requireNonNull(retrieverConnection, "No retriever connection provided");
    this.catalog = catalog;
    parsedDataTypeNames = new ConcurrentHashMap<>();
  }

  MutableColumnDataType lookupDataType(
      final Schema schema, final String databaseSpecificTypeName, final int dataType) {

    final SimpleDataTypeName parsedDataTypeName = parseDataTypeName(databaseSpecificTypeName);

    final Schema lookupSchema = parsedDataTypeName.schema();
    final String lookupTypeName = parsedDataTypeName.dataTypeName();

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
      columnDataType = new MutableColumnDataType(schema, lookupTypeName, user_defined);
      LOGGER.log(
          Level.FINE,
          new StringFormat("Creating data-type from column or parameter <%s>", columnDataType));
      setDataTypeFields(columnDataType, dataType, null);
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
  MutableColumnDataType lookupOrCreateDataType(
      final Schema schema,
      final String databaseSpecificTypeName,
      final DataTypeType type,
      final int javaSqlTypeInt,
      final String mappedClassName) {
    final MutableColumnDataType columnDataType =
        constructColumnDataTypeForCreate(schema, databaseSpecificTypeName, type);
    // If new data type, fill the fields
    final boolean isNewColumnDataType =
        catalog
            .lookupColumnDataType(columnDataType.getSchema(), columnDataType.getName())
            .isEmpty();
    if (isNewColumnDataType) {
      LOGGER.log(Level.FINE, new StringFormat("Creating %s data-type <%s>", type, columnDataType));
      setDataTypeFields(columnDataType, javaSqlTypeInt, mappedClassName);
      catalog.addColumnDataType(columnDataType);
    }
    return columnDataType;
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
  private MutableColumnDataType constructColumnDataTypeForCreate(
      final Schema schema, final String databaseSpecificTypeName, final DataTypeType type) {

    final SimpleDataTypeName parsedDataTypeName = parseDataTypeName(databaseSpecificTypeName);

    final Schema lookupSchema =
        parsedDataTypeName.hasSchema() ? parsedDataTypeName.schema() : schema;
    final String lookupTypeName = parsedDataTypeName.dataTypeName();

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

  private SimpleDataTypeName parseDataTypeName(final String databaseSpecificTypeName) {

    // Default schema to use if schema is not found
    final SchemaReference unspecifiedSchema = null;

    // Check for null values and return early
    if (databaseSpecificTypeName == null) {
      return new SimpleDataTypeName(unspecifiedSchema, "");
    }
    // Use cache to return a parsed result early
    if (parsedDataTypeNames.containsKey(databaseSpecificTypeName)) {
      return parsedDataTypeNames.get(databaseSpecificTypeName);
    }

    if (!databaseSpecificTypeName.contains(".")) {
      // Cache and return
      final SimpleDataTypeName parsedDataTypeName =
          new SimpleDataTypeName(unspecifiedSchema, databaseSpecificTypeName);
      parsedDataTypeNames.put(databaseSpecificTypeName, parsedDataTypeName);
      return parsedDataTypeName;
    }

    // PostgreSQL and IBM DB2 may quote column data type names, so "unquote" them
    final Identifiers identifiers = retrieverConnection.getIdentifiers();
    final String[] splitName = splitList(databaseSpecificTypeName, "\\.");
    if (splitName.length == 0) {
      return new SimpleDataTypeName(unspecifiedSchema, databaseSpecificTypeName);
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
    final String simpleTypeName = splitName[splitName.length - 1];

    final SimpleDataTypeName parsedDataTypeName =
        new SimpleDataTypeName(lookupSchema, simpleTypeName);
    parsedDataTypeNames.put(databaseSpecificTypeName, parsedDataTypeName);

    return parsedDataTypeName;
  }

  private void setDataTypeFields(
      final MutableColumnDataType columnDataType,
      final int javaSqlTypeInt,
      final String mappedClassName) {
    final JavaSqlType javaSqlType = retrieverConnection.getJavaSqlTypes().valueOf(javaSqlTypeInt);
    columnDataType.setJavaSqlType(javaSqlType);
    if (isBlank(mappedClassName)) {
      final Class<?> mappedClass;
      final String dataTypeName = columnDataType.getName();
      final TypeMap typeMap = retrieverConnection.getTypeMap();
      if (typeMap.containsKey(dataTypeName)) {
        mappedClass = typeMap.get(dataTypeName);
      } else {
        mappedClass = typeMap.get(javaSqlType.getName());
      }
      columnDataType.setTypeMappedClass(mappedClass);
    } else {
      columnDataType.setTypeMappedClass(mappedClassName);
    }
    columnDataType.withQuoting(retrieverConnection.getIdentifiers());
  }
}
