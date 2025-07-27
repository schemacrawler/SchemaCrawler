/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.operation;

import static schemacrawler.loader.counts.TableRowCountsUtility.getRowCountMessage;
import static schemacrawler.tools.command.text.schema.options.SchemaTextDetailType.schema;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import schemacrawler.crawl.MetadataResultSet;
import schemacrawler.crawl.RetrievalCounts;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.tools.command.text.operation.options.Operation;
import schemacrawler.tools.command.text.operation.options.OperationOptions;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.formatter.base.BaseTabularFormatter;
import schemacrawler.tools.text.formatter.base.helper.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.traversal.DataTraversalHandler;
import us.fatehi.utility.Color;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.html.Alignment;
import us.fatehi.utility.string.StringFormat;

/** Text formatting of data. */
public final class DataTextFormatter extends BaseTabularFormatter<OperationOptions>
    implements DataTraversalHandler {

  private static final Logger LOGGER = Logger.getLogger(DataTextFormatter.class.getName());

  private static String getMessage(final double aggregate) {
    final Number number;
    if (Math.abs(aggregate - (int) aggregate) < 1E-10D) {
      number = Integer.valueOf((int) aggregate);
    } else {
      number = Double.valueOf(aggregate);
    }
    final String message = getRowCountMessage(number);
    return message;
  }

  private final Operation operation;
  private int dataBlockCount;

  /**
   * Text formatting of data.
   *
   * @param operation Options for text formatting of data
   * @param options Options for text formatting of data
   * @param outputOptions Options for text formatting of data
   * @param identifierQuoteString Quote character for identifier
   */
  public DataTextFormatter(
      final Operation operation,
      final OperationOptions options,
      final OutputOptions outputOptions,
      final Identifiers identifiers) {
    super(schema, options, outputOptions, identifiers);
    this.operation = requireNonNull(operation, "No operation provided");
  }

  /** {@inheritDoc} */
  @Override
  public void end() {
    if (operation == OperationType.count) {
      formattingHelper.writeObjectEnd();
    }

    super.end();
  }

  /** {@inheritDoc} */
  @Override
  public void handleData(final Query query, final ResultSet rows) {
    final String title;
    if (query != null) {
      title = query.getName();
    } else {
      title = "";
    }

    handleData(title, rows);
  }

  /** {@inheritDoc} */
  @Override
  public void handleData(final Table table, final ResultSet rows) {
    final String tableName;
    if (table != null) {
      tableName = quoteName(table);
    } else {
      tableName = "";
    }

    handleData(tableName, rows);
  }

  private void handleData(final String title, final ResultSet rows) {
    if (rows == null) {
      return;
    }

    if (dataBlockCount == 0) {
      printHeader();
    }

    if (operation == OperationType.count) {
      handleTableAggregateData(title, rows);
    } else {
      handleTableData(title, rows);
    }

    dataBlockCount = dataBlockCount + 1;
  }

  /**
   * Handles an aggregate operation, such as a count, for a given table.
   *
   * @param title Title
   * @param results Results
   */
  private void handleTableAggregateData(final String title, final ResultSet results) {
    long aggregate;
    try {
      aggregate = DatabaseUtility.readResultsForLong(title, results);
    } catch (final SQLException e) {
      LOGGER.log(
          Level.WARNING, e, new StringFormat("Could not obtain aggregate data for <%s>", title));
      aggregate = 0;
    }
    final String message = getMessage(aggregate);
    formattingHelper.writeNameValueRow(title, message, Alignment.right);
  }

  private void handleTableData(final String title, final ResultSet rows) {
    formattingHelper.println();
    formattingHelper.println();
    formattingHelper.writeObjectStart();
    formattingHelper.writeObjectNameRow("", title, "", Color.white);

    final String name = String.format("Data for %s for <%s>", operation, title);
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name.toLowerCase());
    try (final MetadataResultSet dataRows = new MetadataResultSet(rows, name)) {
      dataRows.setShowLobs(options.isShowLobs());
      dataRows.setMaxRows(options.getMaxRows());

      formattingHelper.writeRowHeader(quoteColumnNames(dataRows.getColumnNames()));

      while (dataRows.next()) {
        final List<Object> currentRow = dataRows.row();
        final Object[] columnData = currentRow.toArray();
        formattingHelper.writeRow(columnData);
        retrievalCounts.countIncluded();
      }
    } catch (final SQLException e) {
      throw new DatabaseAccessException(String.format("Could not handle rows for <%s>", title), e);
    }
    formattingHelper.writeObjectEnd();

    retrievalCounts.log();
  }

  private void printHeader() {
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, operation.getTitle());

    if (operation == OperationType.count) {
      formattingHelper.writeObjectStart();
      formattingHelper.writeObjectNameRow("", operation.getTitle(), "", Color.white);
    }
  }

  private String[] quoteColumnNames(final String[] columnNames) {
    final String[] quotedColumnNames = Arrays.copyOf(columnNames, columnNames.length);
    for (int i = 0; i < columnNames.length; i++) {
      final String columnName = columnNames[i];
      final String quotedColumnName = identifiers.quoteName(columnName);
      quotedColumnNames[i] = quotedColumnName;
    }
    return quotedColumnNames;
  }
}
