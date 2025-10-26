/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.operation;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
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
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.command.text.operation.options.Operation;
import schemacrawler.tools.command.text.operation.options.OperationOptions;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.traversal.DataTraversalHandler;
import schemacrawler.utility.BinaryData;
import us.fatehi.utility.Utility;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.string.StringFormat;

/** JSON formatting of data. */
public final class DataJsonFormatter implements DataTraversalHandler {

  private static final Logger LOGGER = Logger.getLogger(DataJsonFormatter.class.getName());

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
  public DataJsonFormatter(
      final Operation operation,
      final OperationOptions options,
      final OutputOptions outputOptions) {

    this.options = requireNonNull(options, "Operation options not provided");
    requireNonNull(outputOptions, "Output options not provided");

    this.operation = requireNonNull(operation, "No operation provided");

    try {
      final PrintWriter out = outputOptions.openNewOutputWriter(false);
      final JsonFactory factory = new JsonFactory();
      generator = factory.createGenerator(out);
      generator.useDefaultPrettyPrinter();
      LOGGER.log(Level.CONFIG, generator.version().toFullString());
    } catch (final IOException e) {
      throw new IORuntimeException("Could not create JSON formatter", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void begin() {
    try {
      generator.writeStartObject();
    } catch (final IOException e) {
      throw new IORuntimeException("Could not write JSON object", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void end() {
    try {
      generator.writeEndObject();
      generator.flush();
      generator.close();
    } catch (final IOException e) {
      throw new IORuntimeException("Could not close JSON object", e);
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
          generator.writeStringField("query", title);
        } catch (final IOException e) {
          throw new IORuntimeException("Could not write query name <%s>".formatted(title), e);
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
        generator.writeStringField(tableType, tableName);
        generator.writeStringField("schema", table.getSchema().getFullName());
      } catch (final IOException e) {
        throw new IORuntimeException("Could not write table name <%s>".formatted(tableName), e);
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
      generator.writeStringField("db", crawlInfo.getDatabaseVersion().getProductName());
      generator.writeStringField("operation", operation.toString());
      generator.flush();
    } catch (final IOException e) {
      throw new IORuntimeException("Could not write database information", e);
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
      generator.writeNumberField(operation.getName(), aggregate);
    } catch (final IOException e) {
      throw new IORuntimeException("Could notcount for table".formatted(title), e);
    }
  }

  private void handleTableData(final String title, final ResultSet rows) {
    try {
      final String name = "Data for %s for <%s>".formatted(operation, title);
      final RetrievalCounts retrievalCounts = new RetrievalCounts(name.toLowerCase());
      generator.writeFieldName("data");
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
            generator.writeStringField(columnNames[i], elementData);
          }
          generator.writeEndObject();
          retrievalCounts.countIncluded();
        }
      } catch (final SQLException e) {
        throw new DatabaseAccessException("Could not handle rows for <%s>".formatted(title), e);
      }
      generator.writeEndArray();

      retrievalCounts.log();
    } catch (final IOException e) {
      throw new IORuntimeException(
          "Could not write data in JSON format for <%s>".formatted(title), e);
    }
  }

  private void writeEndDataBlock() {
    try {
      generator.writeEndObject();
      generator.flush();
    } catch (final IOException e) {
      throw new IORuntimeException("Could not write end of data block", e);
    }
  }

  private void writeStartDataBlock() {
    try {
      generator.writeFieldId(dataBlockCount);
      generator.writeStartObject();
    } catch (final IOException e) {
      throw new IORuntimeException("Could not write start of data block", e);
    }

    dataBlockCount = dataBlockCount + 1;
  }
}
