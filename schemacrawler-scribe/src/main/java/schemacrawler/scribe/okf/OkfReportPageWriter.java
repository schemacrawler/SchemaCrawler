/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.okf;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.scribe.model.CrossReferenceModelFactory;
import schemacrawler.scribe.model.LintModel;
import schemacrawler.scribe.model.LintModelFactory;
import schemacrawler.scribe.renderer.ScribeMessages;
import schemacrawler.scribe.renderer.ScribeSupport;
import us.fatehi.utility.string.StringFormat;

/** Writes OKF index and report pages. */
public final class OkfReportPageWriter {

  private static final Logger LOGGER = Logger.getLogger(OkfReportPageWriter.class.getName());

  private static final String ROOT_INDEX_PATH = "index.md";
  private static final String TABLES_INDEX_PATH = "tables/index.md";
  private static final String ROUTINES_INDEX_PATH = "routines/index.md";
  private static final String REPORTS_DIRECTORY = "reports/";
  private static final String REPORTS_INDEX_PATH = REPORTS_DIRECTORY + "index.md";
  private static final String LINT_PATH = REPORTS_DIRECTORY + "lint.md";
  private static final String SCHEMA_DIAGRAM_PATH = REPORTS_DIRECTORY + "schema.md";
  private static final String CROSS_REFERENCES_PATH = REPORTS_DIRECTORY + "cross-references.md";

  private final ScribeSupport support;
  private final OkfTemplateRenderer templateRenderer;

  public OkfReportPageWriter(
      final ScribeSupport support, final OkfTemplateRenderer templateRenderer) {
    this.support = requireNonNull(support, "No Scribe support provided");
    this.templateRenderer = requireNonNull(templateRenderer, "No template renderer provided");
  }

  public void writeReportAndIndexPages() {
    writeIndexes();
    writeCrossReferences();
    writeSchemaDiagram();
    if (support.isLintEnabled()) {
      writeLintReport();
    }
  }

  private Map<String, Object> makeModel() {
    final ScribeMessages msg = support.messages();
    final Map<String, Object> model = new HashMap<>();
    model.put("support", support);
    model.put("msg", msg);
    model.put("catalog", support.getCatalog());
    model.put("er_model", support.getERModel());
    model.put("title", support.databaseTitle());
    return model;
  }

  private void writeCrossReferences() {
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(
          Level.FINE,
          new StringFormat("Writing cross-reference index to <%s>", CROSS_REFERENCES_PATH));
    }

    final Map<String, Object> model = makeModel();
    model.put(
        "crossReferenceEntries", CrossReferenceModelFactory.createCrossReferenceModel(support));
    templateRenderer.writeTemplate("cross-references.ftl", model, CROSS_REFERENCES_PATH);
  }

  private void writeIndexes() {
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, new StringFormat("Writing OKF index files"));
    }

    final List<Table> tablesAndViews = support.allTablesAndViews();
    final List<Routine> routines = support.allRoutines();

    final Map<String, Object> model = makeModel();
    model.put("tables", tablesAndViews);
    model.put("routines", routines);

    templateRenderer.writeTemplate("root-index.ftl", model, ROOT_INDEX_PATH);
    templateRenderer.writeTemplate("tables-index.ftl", model, TABLES_INDEX_PATH);
    templateRenderer.writeTemplate("routines-index.ftl", model, ROUTINES_INDEX_PATH);
    templateRenderer.writeTemplate("reports-index.ftl", model, REPORTS_INDEX_PATH);
  }

  private void writeLintReport() {
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, new StringFormat("Writing lint report to <%s>", LINT_PATH));
    }

    final LintModel lintModel = LintModelFactory.createLintModel(support);
    final Map<String, Object> model = makeModel();
    model.put("lintCount", lintModel.lintCount());
    model.put("lintGroups", lintModel.lintGroups());

    templateRenderer.writeTemplate("lint.ftl", model, LINT_PATH);
  }

  private void writeSchemaDiagram() {
    final Map<String, Object> model = makeModel();
    templateRenderer.writeTemplate("schema-diagram.ftl", model, SCHEMA_DIAGRAM_PATH);
  }
}
