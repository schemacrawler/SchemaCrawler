/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.operation;

import static java.util.Objects.requireNonNull;
import static tools.jackson.core.StreamReadFeature.IGNORE_UNDEFINED;
import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static tools.jackson.core.StreamWriteFeature.IGNORE_UNKNOWN;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;
import static us.fatehi.utility.Utility.isBlank;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.crawl.MetadataResultSet;
import schemacrawler.crawl.RetrievalCounts;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.text.operation.options.Operation;
import schemacrawler.tools.command.text.operation.options.OperationOptions;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.traversal.DataTraversalHandler;
import schemacrawler.utility.BinaryData;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import us.fatehi.utility.Utility;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.string.StringFormat;

public abstract class BaseJacksonFormatter implements DataTraversalHandler {

  private static final Logger LOGGER = Logger.getLogger(BaseJacksonFormatter.class.getName());

  private final OperationOptions options;
  private final JsonGenerator generator;
  private final Operation operation;
  private int dataBlockCount;

  /**
   * JSON formatting of data.
   *
   * @param operation Operation type
   * @param options Options for text formatting of data
   * @param outputOptions Options for text formatting of data
   * @param identifierQuoteString Quote character for identifier
   */
  public BaseJacksonFormatter(
      final Operation operation,
      final OperationOptions options,
      final OutputOptions outputOptions) {

    this.options = requireNonNull(options, "Operation options not provided");
    requireNonNull(outputOptions, "Output options not provided");

    this.operation = requireNonNull(operation, "No operation provided");

    try {
      final PrintWriter out = outputOptions.openNewOutputWriter(false);
      generator = newConfiguredObjectMapper(newMapperBuilder()).createGenerator(out);

      LOGGER.log(Level.CONFIG, generator.version().toFullString());
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException("Could not create JSON formatter", e);
    }
  }

  private static ObjectMapper newConfiguredObjectMapper(
      final MapperBuilder<? extends ObjectMapper, ?> mapperBuilder) {

    requireNonNull(mapperBuilder, "No mapper builder provided");
    mapperBuilder.enable(ORDER_MAP_ENTRIES_BY_KEYS, INDENT_OUTPUT, USE_EQUALITY_FOR_OBJECT_ID);
    mapperBuilder.disable(FAIL_ON_NULL_FOR_PRIMITIVES);
    mapperBuilder.enable(INCLUDE_SOURCE_IN_LOCATION, IGNORE_UNDEFINED);
    mapperBuilder.enable(IGNORE_UNKNOWN);

    final ObjectMapper objectMapper = mapperBuilder.build();
    return objectMapper;
  }

  protected abstract MapperBuilder<? extends ObjectMapper, ?> newMapperBuilder();

  /** {@inheritDoc} */
  @Override
  public void begin() {
    try {
      generator.writeStartObject();
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException("Could not write JSON object", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void end() {
    try {
      generator.writeEndObject();
      generator.flush();
      generator.close();
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException("Could not close JSON object", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void handleData(final Query query, final ResultSet rows) {

    writeStartDataBlock();

    final String title;
    if (query != null) {
      title = query.name();
      if (!isBlank(title)) {
        try {
          generator.writeStringProperty("query", title);
        } catch (final JacksonException e) {
          throw new ExecutionRuntimeException(
              "Could not write query name <%s>".formatted(title), e);
        }
      }
    } else {
      title = "";
    }

    handleData(title, rows);

    writeEndDataBlock();
  }

  /** {@inheritDoc} */
  @Override
  public void handleData(final Table table, final ResultSet rows) {

    writeStartDataBlock();

    final String tableName;
    if (table != null) {
      tableName = table.getName();
      try {
        final String tableType = Utility.toSnakeCase(table.getTableType().toString());
        generator.writeStringProperty(tableType, tableName);
        generator.writeStringProperty("schema", table.getSchema().getFullName());
      } catch (final JacksonException e) {
        throw new ExecutionRuntimeException(
            "Could not write table name <%s>".formatted(tableName), e);
      }
    } else {
      tableName = "";
    }

    handleData(tableName, rows);

    writeEndDataBlock();
  }

  /** {@inheritDoc} */
  @Override
  public void handleHeader(final CrawlInfo crawlInfo) {
    if (crawlInfo == null) {
      return;
    }
    try {
      generator.writeStringProperty("db", crawlInfo.getDatabaseVersion().getProductName());
      generator.writeStringProperty("operation", operation.toString());
      generator.flush();
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException("Could not write database information", e);
    }
  }

  @Override
  public void handleHeaderEnd() {
    // No-op
  }

  @Override
  public void handleHeaderStart() {
    // No-op
  }

  private void handleData(final String tableName, final ResultSet rows) {
    if (rows == null) {
      return;
    }

    if (operation == OperationType.count) {
      handleTableAggregate(tableName, rows);
    } else {
      handleTableData(tableName, rows);
    }
  }

  /**
   * Handles an aggregate operation, such as a count, for a given table.
   *
   * @param title Title
   * @param results Results
   */
  private void handleTableAggregate(final String title, final ResultSet results) {
    long aggregate = 0;
    try {
      aggregate = DatabaseUtility.readResultsForLong(title, results);
    } catch (final SQLException e) {
      LOGGER.log(
          Level.WARNING, e, new StringFormat("Could not obtain aggregate data for <%s>", title));
      aggregate = 0;
    }
    try {
      generator.writeNumberProperty(operation.getName(), aggregate);
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException("Could notcount for table".formatted(title), e);
    }
  }

  private void handleTableData(final String title, final ResultSet rows) {
    try {
      final String name = "Data for %s for <%s>".formatted(operation, title);
      final RetrievalCounts retrievalCounts = new RetrievalCounts(name.toLowerCase());
      generator.writeName("data");
      generator.writeStartArray();
      try (final MetadataResultSet dataRows = new MetadataResultSet(rows, name)) {
        dataRows.setShowLobs(options.isShowLobs());
        dataRows.setMaxRows(options.getMaxRows());
        while (dataRows.next()) {
          retrievalCounts.count();
          generator.writeStartObject();
          final String[] columnNames = dataRows.getColumnNames();
          final List<Object> currentRow = dataRows.row();
          for (int i = 0; i < columnNames.length; i++) {
            final Object element = currentRow.get(i);
            final String elementData;
            if (element == null) {
              elementData = null;
            } else if (element instanceof BinaryData) {
              elementData = "<BINARY DATA>";
            } else {
              elementData = element.toString();
            }
            generator.writeStringProperty(columnNames[i], elementData);
          }
          generator.writeEndObject();
          retrievalCounts.countIncluded();
        }
      } catch (final SQLException e) {
        throw new DatabaseAccessException("Could not handle rows for <%s>".formatted(title), e);
      }
      generator.writeEndArray();

      retrievalCounts.log();
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException(
          "Could not write data in JSON format for <%s>".formatted(title), e);
    }
  }

  private void writeEndDataBlock() {
    try {
      generator.writeEndObject();
      generator.flush();
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException("Could not write end of data block", e);
    }
  }

  private void writeStartDataBlock() {
    try {
      generator.writePropertyId(dataBlockCount);
      generator.writeStartObject();
    } catch (final JacksonException e) {
      throw new ExecutionRuntimeException("Could not write start of data block", e);
    }

    dataBlockCount = dataBlockCount + 1;
  }
}
