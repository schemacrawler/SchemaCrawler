/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.base;

import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.formatter.base.helper.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.text.options.BaseTextOptions;
import us.fatehi.utility.html.Alignment;

/** Text formatting of schema. */
public abstract class BaseTabularFormatter<O extends BaseTextOptions> extends BaseFormatter<O> {

  protected BaseTabularFormatter(
      final SchemaTextDetailType schemaTextDetailType,
      final O options,
      final OutputOptions outputOptions,
      final Identifiers identifiers) {
    super(schemaTextDetailType, options, outputOptions, identifiers);
  }

  /** {@inheritDoc} */
  @Override
  public void begin() {
    formattingHelper.writeDocumentStart();
  }

  /** {@inheritDoc} */
  @Override
  public void end() {
    formattingHelper.writeDocumentEnd();
    super.end();
  }

  @Override
  public final void handleHeader(final CrawlInfo crawlInfo) {
    if (crawlInfo == null) {
      return;
    }

    final String title = outputOptions.getTitle();
    if (!isBlank(title)) {
      formattingHelper.writeHeader(DocumentHeaderType.title, title);
    }

    if (options.isNoInfo()
        || options.isNoSchemaCrawlerInfo()
            && !options.isShowDatabaseInfo()
            && !options.isShowJdbcDriverInfo()) {
      return;
    }

    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "System Information");

    formattingHelper.writeObjectStart();

    if (!options.isNoSchemaCrawlerInfo()) {
      formattingHelper.writeNameValueRow(
          "generated by", crawlInfo.getSchemaCrawlerVersion().toString(), Alignment.inherit);
      formattingHelper.writeNameValueRow(
          "generated on", crawlInfo.getCrawlTimestamp(), Alignment.inherit);
    }

    if (options.isShowDatabaseInfo()) {
      formattingHelper.writeNameValueRow(
          "database version", crawlInfo.getDatabaseVersion().toString(), Alignment.inherit);
    }

    if (options.isShowJdbcDriverInfo()) {
      formattingHelper.writeNameValueRow(
          "driver version", crawlInfo.getJdbcDriverVersion().toString(), Alignment.inherit);
    }

    formattingHelper.writeObjectEnd();
  }

  @Override
  public void handleHeaderEnd() {
    // Default implementation - NO-OP
  }

  @Override
  public void handleHeaderStart() {
    // Default implementation - NO-OP
  }

  protected final boolean printVerboseDatabaseInfo() {
    return !options.isNoInfo() && schemaTextDetailType == SchemaTextDetailType.details;
  }
}
