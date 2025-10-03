/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.options;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.UUID;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import us.fatehi.utility.ObjectToString;
import us.fatehi.utility.ioresource.FileOutputResource;
import us.fatehi.utility.ioresource.OutputResource;

public final class OutputOptions implements Options {

  private final Charset inputEncodingCharset;
  private final Charset outputEncodingCharset;
  private final String outputFormatValue;
  private final OutputResource outputResource;
  private final String title;

  OutputOptions(
      final Charset inputEncodingCharset,
      final OutputResource outputResource,
      final Charset outputEncodingCharset,
      final String outputFormatValue,
      final String title) {
    this.inputEncodingCharset = requireNonNull(inputEncodingCharset, "No input encoding provided");
    this.outputResource = requireNonNull(outputResource, "No output resource provided");
    this.outputEncodingCharset =
        requireNonNull(outputEncodingCharset, "No output encoding provided");
    this.outputFormatValue = requireNonNull(outputFormatValue, "No output format value provided");
    this.title = title;
  }

  /** Character encoding for input files, such as scripts and templates. */
  public Charset getInputCharset() {
    return inputEncodingCharset;
  }

  /** Character encoding for output files. */
  public Charset getOutputCharset() {
    return outputEncodingCharset;
  }

  public Path getOutputFile(final String extension) {
    final Path outputFile;
    if (outputResource instanceof FileOutputResource resource) {
      outputFile = resource.getOutputFile();
    } else {
      outputFile =
          Path.of(".", "schemacrawler-%s.%s".formatted(UUID.randomUUID(), trimToEmpty(extension)))
              .normalize()
              .toAbsolutePath();
    }
    return outputFile;
  }

  /**
   * Gets the output format value.
   *
   * @return Output format value.
   */
  public String getOutputFormatValue() {
    return outputFormatValue;
  }

  /**
   * Title for the output.
   *
   * @return Title for the output
   */
  public String getTitle() {
    return title;
  }

  /**
   * Checks whether there is a title for the output.
   *
   * @return Whether there is a title
   */
  public boolean hasTitle() {
    return !isBlank(title);
  }

  /**
   * Gets the output reader. If the output resource is null, first set it to console output.
   *
   * @return Output writer
   * @throws IOException On an exception
   */
  public PrintWriter openNewOutputWriter() {
    return openNewOutputWriter(false);
  }

  /** Gets the output reader. If the output resource is null, first set it to console output. */
  public PrintWriter openNewOutputWriter(final boolean appendOutput) {
    try {
      return new PrintWriter(
          outputResource.openNewOutputWriter(getOutputCharset(), appendOutput), true);
    } catch (final IOException e) {
      throw new IORuntimeException(
          "Could not open output writer: <%s>".formatted(e.getMessage()), e);
    }
  }

  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }

  OutputResource getOutputResource() {
    return outputResource;
  }
}
