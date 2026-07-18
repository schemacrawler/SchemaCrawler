/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.okf;

import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.output.ScribeOutputContext;
import schemacrawler.scribe.renderer.ScribeRenderer;
import schemacrawler.scribe.renderer.ScribeSupport;

/** Renders a Google OKF (Open Knowledge Framework) Markdown bundle. */
public final class OkfScribeRenderer implements ScribeRenderer {

  @Override
  public String getSupportedOutputFormat() {
    return "okf";
  }

  @Override
  public void render(
      final ScribeSupport support, final ScribeOptions options, final ScribeOutputContext output)
      throws SchemaCrawlerException {

    final OkfTemplateRenderer templateRenderer = new OkfTemplateRenderer(output);

    final OkfConceptPageWriter conceptPageWriter =
        new OkfConceptPageWriter(support, templateRenderer);
    conceptPageWriter.writeConceptPages();

    final OkfReportPageWriter reportPageWriter = new OkfReportPageWriter(support, templateRenderer);
    reportPageWriter.writeReportAndIndexPages();
  }
}
