/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.output;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Abstraction over ZIP or filesystem output for a multi-file Scribe report. Renderers write one
 * entry at a time, addressed by a ZIP-relative path, and never see ZIP or filesystem internals.
 */
public interface ScribeOutputContext extends Closeable {

  /**
   * Opens a raw output stream for a new entry at the given path relative within the zip file or
   * folder structure.
   *
   * @param relativePath Path of the new entry, relative to the report root
   * @return Output stream for the new entry
   * @throws IOException On an I/O error
   */
  OutputStream openNewOutputStream(String relativePath) throws IOException;

  /**
   * Opens a UTF-8 writer for a new entry at the given path relative within the zip file or folder
   * structure.
   *
   * @param relativePath Path of the new entry, relative to the report root
   * @return Writer for the new entry
   * @throws IOException On an I/O error
   */
  Writer openNewOutputWriter(String relativePath) throws IOException;
}
