/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.options;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.IOUtility.getFileExtension;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import schemacrawler.schemacrawler.OptionsBuilder;
import us.fatehi.utility.ioresource.ConsoleOutputResource;
import us.fatehi.utility.ioresource.FileOutputResource;
import us.fatehi.utility.ioresource.OutputResource;
import us.fatehi.utility.ioresource.WriterOutputResource;

public final class OutputOptionsBuilder
    implements OptionsBuilder<OutputOptionsBuilder, OutputOptions> {

  public static OutputOptionsBuilder builder() {
    return new OutputOptionsBuilder();
  }

  public static OutputOptionsBuilder builder(final OutputOptions outputOptions) {
    return new OutputOptionsBuilder().fromOptions(outputOptions);
  }

  public static OutputOptions newOutputOptions() {
    return new OutputOptionsBuilder().toOptions();
  }

  public static OutputOptions newOutputOptions(
      final OutputFormat outputFormat, final Path outputFile) {
    return OutputOptionsBuilder.builder()
        .withOutputFormat(outputFormat)
        .withOutputFile(outputFile)
        .toOptions();
  }

  private OutputResource outputResource;
  private String outputFormatValue;
  private Charset inputEncodingCharset;
  private Charset outputEncodingCharset;
  private String title;

  private OutputOptionsBuilder() {
    // Default values are set at the time of building options
    // All values are set to null, and corrected at the time of
    // converting to options
  }

  @Override
  public OutputOptionsBuilder fromOptions(final OutputOptions options) {
    if (options == null) {
      return this;
    }

    withInputEncoding(options.getInputCharset())
        .withOutputEncoding(options.getOutputCharset())
        .withOutputFormatValue(options.getOutputFormatValue())
        .title(options.getTitle());
    outputResource = options.getOutputResource();

    return this;
  }

  public OutputOptionsBuilder title(final String title) {
    this.title = trimToEmpty(title);
    return this;
  }

  @Override
  public OutputOptions toOptions() {
    withInputEncoding(inputEncodingCharset);
    withOutputResource(outputResource);
    withOutputEncoding(inputEncodingCharset);

    // If there is an output format specified, use it
    // Otherwise, infer the output format from the extension of the file
    // Otherwise, assume text output
    if (isBlank(outputFormatValue)) {
      final String fileExtension;
      if (outputResource instanceof FileOutputResource) {
        fileExtension = getFileExtension(((FileOutputResource) outputResource).getOutputFile());
      } else {
        fileExtension = null;
      }

      outputFormatValue = isBlank(fileExtension) ? "text" : fileExtension;
    }

    if (isBlank(title)) {
      title = "";
    }

    return new OutputOptions(
        inputEncodingCharset, outputResource, outputEncodingCharset, outputFormatValue, title);
  }

  public OutputOptionsBuilder withConsoleOutput() {
    outputResource = new ConsoleOutputResource();
    return this;
  }

  public OutputOptionsBuilder withInputEncoding(final Charset inputCharset) {
    if (inputCharset == null) {
      inputEncodingCharset = UTF_8;
    } else {
      inputEncodingCharset = inputCharset;
    }
    return this;
  }

  /**
   * Set character encoding for input files, such as scripts and templates.
   *
   * @param inputEncoding Input encoding
   * @return Builder
   */
  public OutputOptionsBuilder withInputEncoding(final String inputEncoding) {
    try {
      inputEncodingCharset = Charset.forName(inputEncoding);
    } catch (final IllegalArgumentException e) {
      inputEncodingCharset = UTF_8;
    }
    return this;
  }

  public OutputOptionsBuilder withOutputEncoding(final Charset outputCharset) {
    if (outputCharset == null) {
      outputEncodingCharset = UTF_8;
    } else {
      outputEncodingCharset = outputCharset;
    }
    return this;
  }

  /**
   * Set character encoding for output files.
   *
   * @param outputEncoding Output encoding
   * @return Builder
   */
  public OutputOptionsBuilder withOutputEncoding(final String outputEncoding) {
    try {
      outputEncodingCharset = Charset.forName(outputEncoding);
    } catch (final IllegalArgumentException e) {
      outputEncodingCharset = UTF_8;
    }
    return this;
  }

  /**
   * Sets the name of the output file. It is important to note that the output encoding should be
   * available at this point.
   *
   * @param outputFile Output path.
   * @return Builder
   */
  public OutputOptionsBuilder withOutputFile(final Path outputFile) {
    requireNonNull(outputFile, "No output file provided");
    outputResource = new FileOutputResource(outputFile);
    return this;
  }

  /**
   * Sets output format.
   *
   * @param outputFormat Output format
   * @return Builder
   */
  public OutputOptionsBuilder withOutputFormat(final OutputFormat outputFormat) {
    outputFormatValue = requireNonNull(outputFormat, "No output format provided").getFormat();
    return this;
  }

  /**
   * Sets output format value.
   *
   * @param outputFormatValue Output format value
   * @return Builder
   */
  public OutputOptionsBuilder withOutputFormatValue(final String outputFormatValue) {
    this.outputFormatValue = requireNonNull(outputFormatValue, "No output format value provided");
    return this;
  }

  public OutputOptionsBuilder withOutputResource(final OutputResource outputResource) {
    if (outputResource == null) {
      if (outputFormatValue == null || "text".equals(outputFormatValue)) {
        this.outputResource = new ConsoleOutputResource();
      } else {
        final String extension;
        if ("htmlx".equals(outputFormatValue)) {
          // Tacky hack for htmlx format
          extension = "svg.html";
        } else if (outputFormatValue.matches("[A-Za-z]+")) {
          extension = outputFormatValue;
        } else {
          extension = "out";
        }

        final Path randomOutputFile =
            Paths.get(".", String.format("schemacrawler-%s.%s", UUID.randomUUID(), extension))
                .normalize()
                .toAbsolutePath();
        this.outputResource = new FileOutputResource(randomOutputFile);
      }
    } else {
      this.outputResource = outputResource;
    }
    return this;
  }

  public OutputOptionsBuilder withOutputWriter(final Writer writer) {
    requireNonNull(writer, "No output writer provided");
    outputResource = new WriterOutputResource(writer);
    return this;
  }
}
