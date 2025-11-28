/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.sql.DatabaseMetaData.typeNullable;
import static java.sql.DatabaseMetaData.typeNullableUnknown;
import static java.util.Objects.requireNonNull;
import static schemacrawler.schema.DataTypeType.system;
import static schemacrawler.schema.DataTypeType.user_defined;
import static schemacrawler.schemacrawler.InformationSchemaKey.TYPE_INFO;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.typeInfoRetrievalStrategy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SearchableType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.utility.string.StringFormat;

final class DataTypeRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(DataTypeRetriever.class.getName());

  DataTypeRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  /**
   * Retrieves column data type metadata.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveSystemColumnDataTypes() throws SQLException {
    final Schema systemSchema = new SchemaReference();

    switch (getRetrieverConnection().get(typeInfoRetrievalStrategy)) {
      case data_dictionary_all:
        LOGGER.log(
            Level.INFO,
            "Retrieving system column data types, using fast data dictionary retrieval");
        retrieveSystemColumnDataTypesFromDataDictionary(systemSchema);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving system column data types");
        retrieveSystemColumnDataTypesFromMetadata(systemSchema);
        break;

      default:
        LOGGER.log(Level.INFO, "Not retrieving system column data types");
        break;
    }
  }

  void retrieveUserDefinedColumnDataTypes() throws SQLException {

    final NamedObjectList<SchemaReference> schemas = getAllSchemas();

    final String name = "user-defined column data types";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : schemas) {
      LOGGER.log(
          Level.INFO,
          new StringFormat("Retrieving user-defined data types for schema <%s>", schema));
      requireNonNull(schema, "No schema provided");

      final Optional<SchemaReference> schemaOptional = catalog.lookupSchema(schema.getFullName());
      if (schemaOptional.isEmpty()) {
        LOGGER.log(
            Level.INFO,
            new StringFormat(
                "Cannot locate schema, so not retrieving data types for schema: %s", schema));
        continue;
      }

      LOGGER.log(Level.INFO, new StringFormat("Retrieving data types for schema <%s>", schema));

      final String catalogName = schema.getCatalogName();
      final String schemaName = schema.getName();
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final MetadataResultSet results =
              new MetadataResultSet(
                  connection.getMetaData().getUDTs(catalogName, schemaName, null, null),
                  "DatabaseMetaData::getUDTs"); ) {
        while (results.next()) {
          retrievalCounts.count(schema.key());
          createUserDefinedColumnDataType(results, schema);
          retrievalCounts.countIncluded(schema.key());
        }
      } catch (final SQLException e) {
        logPossiblyUnsupportedSQLFeature(
            new StringFormat("Could not retrieve user-defined column data types"), e);
      }
      retrievalCounts.log(schema.key());
    }
    retrievalCounts.log();
  }

  private void createSystemColumnDataType(
      final MetadataResultSet results, final Schema systemSchema) {
    final String typeName = results.getString("TYPE_NAME");
    final int dataType = results.getInt("DATA_TYPE", 0);
    LOGGER.log(
        Level.FINER,
        new StringFormat("Retrieving data type <%s> with type id %d", typeName, dataType));

    final long precision = results.getLong("PRECISION", 0L);
    final String literalPrefix = results.getString("LITERAL_PREFIX");
    final String literalSuffix = results.getString("LITERAL_SUFFIX");
    final String createParameters = results.getString("CREATE_PARAMS");
    final boolean isNullable = results.getInt("NULLABLE", typeNullableUnknown) == typeNullable;
    final boolean isCaseSensitive = results.getBoolean("CASE_SENSITIVE");
    final SearchableType searchable = results.getEnumFromId("SEARCHABLE", SearchableType.unknown);
    final boolean isUnsigned = results.getBoolean("UNSIGNED_ATTRIBUTE");
    final boolean isFixedPrecisionScale = results.getBoolean("FIXED_PREC_SCALE");
    final boolean isAutoIncremented = results.getBoolean("AUTO_INCREMENT");
    final String localTypeName = results.getString("LOCAL_TYPE_NAME");
    final int minimumScale = results.getInt("MINIMUM_SCALE", 0);
    final int maximumScale = results.getInt("MAXIMUM_SCALE", 0);
    final int numPrecisionRadix = results.getInt("NUM_PREC_RADIX", 0);

    final MutableColumnDataType columnDataType =
        lookupOrCreateColumnDataType(systemSchema, typeName, system, dataType);
    columnDataType.withQuoting(getRetrieverConnection().getIdentifiers());
    // Set the Java SQL type code, but no mapped Java class is
    // available, so use the defaults
    columnDataType.setPrecision(precision);
    columnDataType.setLiteralPrefix(literalPrefix);
    columnDataType.setLiteralSuffix(literalSuffix);
    columnDataType.setCreateParameters(createParameters);
    columnDataType.setNullable(isNullable);
    columnDataType.setCaseSensitive(isCaseSensitive);
    columnDataType.setSearchable(searchable);
    columnDataType.setUnsigned(isUnsigned);
    columnDataType.setFixedPrecisionScale(isFixedPrecisionScale);
    columnDataType.setAutoIncrementable(isAutoIncremented);
    columnDataType.setLocalTypeName(localTypeName);
    columnDataType.setMinimumScale(minimumScale);
    columnDataType.setMaximumScale(maximumScale);
    columnDataType.setNumPrecisionRadix(numPrecisionRadix);

    columnDataType.addAttributes(results.getAttributes());

    catalog.addColumnDataType(columnDataType);
  }

  private void createUserDefinedColumnDataType(
      final MetadataResultSet results, final Schema schema) {
    // "TYPE_CAT", "TYPE_SCHEM"
    final String typeName = results.getString("TYPE_NAME");
    LOGGER.log(Level.FINE, new StringFormat("Retrieving data type <%s.%s>", schema, typeName));
    final int dataType = results.getInt("DATA_TYPE", 0);
    final String className = results.getString("CLASS_NAME");
    final String remarks = results.getString("REMARKS");
    final short baseTypeValue = results.getShort("BASE_TYPE", (short) 0);

    final ColumnDataType baseType;
    if (baseTypeValue != 0) {
      baseType = catalog.lookupBaseColumnDataTypeByType(baseTypeValue);
    } else {
      baseType = null;
    }
    final MutableColumnDataType columnDataType =
        lookupOrCreateColumnDataType(schema, typeName, user_defined, dataType, className);
    columnDataType.withQuoting(getRetrieverConnection().getIdentifiers());

    columnDataType.setBaseType(baseType);
    columnDataType.setRemarks(remarks);

    columnDataType.addAttributes(results.getAttributes());

    catalog.addColumnDataType(columnDataType);
  }

  private void retrieveSystemColumnDataTypesFromDataDictionary(final Schema systemSchema)
      throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(TYPE_INFO)) {
      throw new ExecutionRuntimeException("No system column data types SQL provided");
    }
    String name = "system column data types";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query typeInfoSql = informationSchemaViews.getQuery(TYPE_INFO);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(typeInfoSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        createSystemColumnDataType(results, systemSchema);
        retrievalCounts.countIncluded();
      }
    }
    retrievalCounts.log();
  }

  private void retrieveSystemColumnDataTypesFromMetadata(final Schema systemSchema)
      throws SQLException {
    final String name = "system column data types";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final MetadataResultSet results =
            new MetadataResultSet(
                connection.getMetaData().getTypeInfo(), "DatabaseMetaData::getTypeInfo"); ) {
      while (results.next()) {
        retrievalCounts.count();
        createSystemColumnDataType(results, systemSchema);
        retrievalCounts.countIncluded();
      }
    } catch (final SQLException e) {
      logPossiblyUnsupportedSQLFeature(
          new StringFormat("Could not retrieve system column data types"), e);
    }
    retrievalCounts.log();
  }
}
