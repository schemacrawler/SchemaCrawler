/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.lint;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.filter.TableTypesFilter;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Column;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.config.LinterConfig;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/**
 * Evaluates a catalog and creates lints. This base class has core for visiting a catalog, and
 * creating states.Also contains utility methods for subclasses. Needs to be overridden by custom
 * linters.
 */
public abstract class BaseLinter extends AbstractLinter {

  private static final Logger LOGGER = Logger.getLogger(BaseLinter.class.getName());

  private InclusionRule tableInclusionRule;
  private InclusionRule columnInclusionRule;
  private TableTypesFilter tableTypesFilter;

  protected BaseLinter(final PropertyName linterName, final LintCollector lintCollector) {
    super(linterName, lintCollector);
    setTableTypesFilter(null);
    tableInclusionRule = new IncludeAll();
    columnInclusionRule = new IncludeAll();
  }

  @Override
  public final Void call() {
    start(connection);
    for (final Table table : catalog.getTables()) {
      if (includeTable(table)) {
        lint(table, connection);
      } else {
        LOGGER.log(
            Level.FINE,
            new StringFormat("Excluding table <%s> for lint <%s>", table, getLinterId()));
      }
    }
    end(connection);
    catalog = null;

    return null;
  }

  @Override
  public final void configure(final LinterConfig linterConfig) {
    super.configure(linterConfig);
    if (linterConfig != null) {
      tableInclusionRule = linterConfig.getTableInclusionRule();
      columnInclusionRule = linterConfig.getColumnInclusionRule();
    }
  }

  @Override
  public void initialize() {
    // Default implementation - NO-OP
  }

  protected final void addCatalogLint(final String message) {
    addLint(LintObjectType.catalog, catalog, message, null);
  }

  protected final <V extends Serializable> void addCatalogLint(
      final String message, final V value) {
    addLint(LintObjectType.catalog, catalog, message, value);
  }

  protected final void addTableLint(final Table table, final String message) {
    addLint(LintObjectType.table, table, message, null);
  }

  protected final <V extends Serializable> void addTableLint(
      final Table table, final String message, final V value) {
    addLint(LintObjectType.table, table, message, value);
  }

  protected void end(final Connection connection) {
    // Default implementation - NO-OP
  }

  protected final List<Column> getColumns(final Table table) {
    if (table == null) {
      return Collections.emptyList();
    }

    final List<Column> columns = new ArrayList<>(table.getColumns());
    for (final Iterator<Column> iterator = columns.iterator(); iterator.hasNext(); ) {
      final Column column = iterator.next();
      if (!includeColumn(column)) {
        iterator.remove();
      }
    }
    return columns;
  }

  /**
   * Allow linters to take appropriate action based on system information such as type of database,
   * JDBC driver, JVM version or operating system.
   *
   * @return SchemaCrawler crawl information.
   */
  protected final CrawlInfo getCrawlInfo() {
    if (!hasCrawlInfo()) {
      return null;
    }
    return catalog.getCrawlInfo();
  }

  protected final boolean hasCrawlInfo() {
    return catalog != null && catalog.getCrawlInfo() != null;
  }

  protected abstract void lint(Table table, Connection connection);

  protected final void setTableTypesFilter(final TableTypesFilter tableTypesFilter) {
    if (tableTypesFilter == null) {
      this.tableTypesFilter = new TableTypesFilter();
    } else {
      this.tableTypesFilter = tableTypesFilter;
    }
  }

  protected void start(final Connection connection) {
    // Default implementation - NO-OP
  }

  private final boolean includeColumn(final Column column) {
    return column != null && columnInclusionRule.test(column.getFullName());
  }

  private final boolean includeTable(final Table table) {
    return table != null
        && tableInclusionRule.test(table.getFullName())
        && tableTypesFilter.test(table);
  }
}
