/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.renderer;

import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.scribe.command.options.ScribeOptions;
import schemacrawler.scribe.output.ScribeOutputContext;

/** Renderer abstraction for a Scribe report output format. */
public interface ScribeRenderer {

  /**
   * Renders the full multi-file report into the given output context. Renderers must not build
   * their own {@link ScribeSupport} instance; the command builds a single instance and passes it
   * in, so that all renderers see the same catalog, ER model, lint, and message state.
   *
   * @param support Single source of truth for all catalog, ER model, lint, and message data
   * @param options Scribe options
   * @param output ZIP or filesystem output context
   * @throws SchemaCrawlerException On rendering failure
   */
  void render(ScribeSupport support, ScribeOptions options, ScribeOutputContext output)
      throws SchemaCrawlerException;
}
