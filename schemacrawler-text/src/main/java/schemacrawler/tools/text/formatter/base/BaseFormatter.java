/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.tools.text.formatter.base;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.hasNoUpperCase;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.formatter.base.helper.HtmlFormattingHelper;
import schemacrawler.tools.text.formatter.base.helper.PlainTextFormattingHelper;
import schemacrawler.tools.text.formatter.base.helper.TextFormattingHelper;
import schemacrawler.tools.text.options.BaseTextOptions;
import schemacrawler.tools.text.options.DatabaseObjectColorMap;
import schemacrawler.tools.traversal.TraversalHandler;

public abstract class BaseFormatter<O extends BaseTextOptions> implements TraversalHandler {

  private static final Logger LOGGER = Logger.getLogger(BaseFormatter.class.getName());

  protected final O options;
  protected final SchemaTextDetailType schemaTextDetailType;
  protected final OutputOptions outputOptions;
  protected final TextFormattingHelper formattingHelper;
  protected final DatabaseObjectColorMap colorMap;
  protected final Identifiers identifiers;
  private final PrintWriter out;

  protected BaseFormatter(
      final SchemaTextDetailType schemaTextDetailType,
      final O options,
      final OutputOptions outputOptions,
      final Identifiers identifiers) {

    this.options = requireNonNull(options, "Options not provided");
    this.schemaTextDetailType =
        requireNonNull(schemaTextDetailType, "SchemaTextDetailType not provided");
    this.outputOptions = requireNonNull(outputOptions, "Output options not provided");
    colorMap = options.getColorMap();

    final IdentifiersBuilder identifiersBuilder =
        Identifiers.identifiers()
            .fromOptions(identifiers)
            .withIdentifierQuotingStrategy(options.getIdentifierQuotingStrategy());
    this.identifiers = identifiersBuilder.build();

    out = outputOptions.openNewOutputWriter(false);

    final TextOutputFormat outputFormat =
        TextOutputFormat.fromFormat(outputOptions.getOutputFormatValue());
    switch (outputFormat) {
      case html:
        formattingHelper = new HtmlFormattingHelper(out, outputFormat);
        break;
      case text:
      default:
        formattingHelper = new PlainTextFormattingHelper(out, outputFormat);
        break;
    }
  }

  @Override
  public void end() {
    LOGGER.log(Level.INFO, "Closing writer");
    out.flush();
    out.close();
  }

  protected String columnNullable(final String columnTypeName, final boolean isNullable) {
    final String columnNullable;
    if (isNullable) {
      columnNullable = "";
    } else if (hasNoUpperCase(columnTypeName)) {
      columnNullable = " not null";
    } else {
      columnNullable = " NOT NULL";
    }

    return columnNullable;
  }

  protected boolean isBrief() {
    return schemaTextDetailType == SchemaTextDetailType.brief;
  }

  protected boolean isColumnSignificant(final Column column) {
    if (column == null) {
      return false;
    }
    if (!isBrief()) {
      return true;
    }
    return column instanceof IndexColumn
        || column.isPartOfPrimaryKey()
        || column.isPartOfForeignKey()
        || column.isPartOfIndex();
  }

  protected boolean isTableFiltered(final Table table) {
    return table.getAttribute("schemacrawler.filtered_out", false)
        || table instanceof PartialDatabaseObject;
  }

  protected boolean isVerbose() {
    return schemaTextDetailType == SchemaTextDetailType.details;
  }

  protected String nodeId(final DatabaseObject dbObject) {
    if (dbObject == null) {
      return "";
    } else {
      return dbObject.key().slug();
    }
  }

  protected String quoteName(final DatabaseObject table) {
    final String tableName;
    if (options.isShowUnqualifiedNames()) {
      tableName = identifiers.quoteName(table);
    } else {
      tableName = identifiers.quoteFullName(table);
    }
    return tableName;
  }
}
