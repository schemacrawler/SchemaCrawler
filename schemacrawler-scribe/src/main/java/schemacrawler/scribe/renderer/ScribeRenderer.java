/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.renderer;

import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.scribe.command.options.SchemaScribeOptions;
import schemacrawler.scribe.output.ScribeOutputContext;

/**
 * Service-provider interface for a Scribe report output format. Each output format is a separate
 * module that registers an implementation of this interface via {@link java.util.ServiceLoader}.
 */
public interface ScribeRenderer {

  /**
   * Gets the output-format string that this renderer handles.
   *
   * @return Output format, for example {@code "okf"}
   */
  String getSupportedOutputFormat();

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
  void render(ScribeSupport support, SchemaScribeOptions options, ScribeOutputContext output)
      throws SchemaCrawlerException;
}
