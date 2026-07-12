/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.output;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import us.fatehi.utility.string.StringFormat;

/**
 * Writes a Scribe report as a single ZIP file, with one ZIP entry per {@link #openWriter} or {@link
 * #openStream} call. Only one entry may be open at a time.
 */
public final class ZipScribeOutputContext implements ScribeOutputContext {

  /**
   * Shields the shared {@link ZipOutputStream} from being closed when an individual entry's writer
   * or stream is closed; closes the ZIP entry instead.
   */
  private static final class EntryOutputStream extends FilterOutputStream {

    private EntryOutputStream(final ZipOutputStream zipOut) {
      super(zipOut);
    }

    @Override
    public void close() throws IOException {
      flush();
      ((ZipOutputStream) out).closeEntry();
    }
  }

  private static final Logger LOGGER = Logger.getLogger(ZipScribeOutputContext.class.getName());

  private final ZipOutputStream zipOut;

  /**
   * Creates (or overwrites) a ZIP file at the given path.
   *
   * @param zipFilePath Path of the ZIP file to write
   * @throws IOException On an I/O error
   */
  public ZipScribeOutputContext(final Path zipFilePath) throws IOException {
    LOGGER.log(Level.FINE, new StringFormat("Writing Scribe report to ZIP file <%s>", zipFilePath));
    zipOut = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipFilePath)));
  }

  /** {@inheritDoc} */
  @Override
  public void close() throws IOException {
    zipOut.finish();
    zipOut.close();
  }

  /** {@inheritDoc} */
  @Override
  public OutputStream openStream(final String relativePath) throws IOException {
    zipOut.putNextEntry(new ZipEntry(relativePath));
    return new EntryOutputStream(zipOut);
  }

  /** {@inheritDoc} */
  @Override
  public Writer openWriter(final String relativePath) throws IOException {
    zipOut.putNextEntry(new ZipEntry(relativePath));
    return new BufferedWriter(
        new OutputStreamWriter(new EntryOutputStream(zipOut), StandardCharsets.UTF_8));
  }
}
