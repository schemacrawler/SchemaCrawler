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
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.scribe.renderer.ScribeSupport;

/** Writes OKF concept pages for tables/views and routines. */
public final class OkfConceptPageWriter {

  private final ScribeSupport support;
  private final OkfTemplateRenderer templateRenderer;

  public OkfConceptPageWriter(
      final ScribeSupport support, final BundleDirectoryOutput outputDirectory) {
    this(support, new OkfTemplateRenderer(outputDirectory));
  }

  public OkfConceptPageWriter(
      final ScribeSupport support, final OkfTemplateRenderer templateRenderer) {
    this.support = requireNonNull(support, "No Scribe support provided");
    this.templateRenderer = requireNonNull(templateRenderer, "No template renderer provided");
  }

  public void writeConceptPages() throws SchemaCrawlerException {
    for (final Table table : support.allTables()) {
      writeTableConcept(table);
    }

    final List<Routine> routines = support.allRoutines();
    for (final Routine routine : routines) {
      writeRoutineConcept(routine);
    }
  }

  public void writeRoutineConcept(final Routine routine) throws SchemaCrawlerException {
    final String resourcePath = "routines/" + routine.key().slug() + ".md";
    final Map<String, Object> model = newModel(resourcePath);
    model.put("routine", routine);
    templateRenderer.writeTemplate("routine-concept.ftl", model, resourcePath);
  }

  public void writeTableConcept(final Table table) throws SchemaCrawlerException {
    final String resourcePath = "tables/" + table.key().slug() + ".md";
    final Map<String, Object> model = newModel(resourcePath);
    model.put("table", table);
    templateRenderer.writeTemplate("table-concept.ftl", model, resourcePath);
  }

  private Map<String, Object> newModel(final String resourcePath) {

    final OkfFrontMatterSupport frontMatter = new OkfFrontMatterSupport();
    support.transferState(frontMatter);

    final Map<String, Object> model = new HashMap<>();
    model.put("support", support);
    model.put("msg", support.messages());
    model.put("frontMatter", frontMatter);
    model.put("resourcePath", resourcePath);
    return model;
  }
}
