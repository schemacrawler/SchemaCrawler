/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.okf;

import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.scribe.renderer.ScribeRenderer;
import schemacrawler.scribe.renderer.ScribeSupport;

/** Renders a Google OKF (Open Knowledge Framework) Markdown bundle. */
public final class OpenKnowledgeFormatRenderer implements ScribeRenderer {

  @Override
  public void render(final ScribeSupport support, final BundleDirectoryOutput outputDirectory)
      throws SchemaCrawlerException {

    final TemplateRenderer templateRenderer = new TemplateRenderer(outputDirectory);

    final ConceptPageWriter conceptPageWriter = new ConceptPageWriter(support, templateRenderer);
    conceptPageWriter.writeConceptPages();

    final ReportPageWriter reportPageWriter = new ReportPageWriter(support, templateRenderer);
    reportPageWriter.writeReportAndIndexPages();
  }
}
