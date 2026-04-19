/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.serialize.options;

import static us.fatehi.utility.Utility.isBlank;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.formatter.serialize.CatalogSerializer;
import schemacrawler.tools.formatter.serialize.CompactSerializedCatalog;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.formatter.serialize.JsonSerializedCatalog;
import schemacrawler.tools.formatter.serialize.YamlSerializedCatalog;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputFormatState;
import us.fatehi.utility.string.StringFormat;

public enum SerializationFormat implements OutputFormat {
  ser("Java serialization", true),
  json("JavaScript Object Notation (JSON) serialization format", false),
  yaml("YAML Ain't Markup Language (YAML) serialization format", false),
  compact_json("Compact JavaScript Object Notation (JSON) serialization format", false),
  ;

  private static final Logger LOGGER = Logger.getLogger(SerializationFormat.class.getName());

  /**
   * Gets the value from the format.
   *
   * @param format Text output format.
   * @return SerializationFormat
   */
  public static SerializationFormat fromFormat(final String format) {
    final SerializationFormat outputFormat = fromFormatOrNull(format);
    if (outputFormat == null) {
      LOGGER.log(Level.CONFIG, new StringFormat("Unknown format <%s>, using default", format));
      return ser;
    }
    return outputFormat;
  }

  /**
   * Checks if the value of the format is supported.
   *
   * @return True if the format is a text output format
   */
  public static boolean isSupportedFormat(final String format) {
    return fromFormatOrNull(format) != null;
  }

  private static SerializationFormat fromFormatOrNull(final String format) {
    if (isBlank(format)) {
      return null;
    }
    for (final SerializationFormat outputFormat : SerializationFormat.values()) {
      if (outputFormat.outputFormatState.isSupportedFormat(format)) {
        return outputFormat;
      }
    }
    return null;
  }

  private final OutputFormatState outputFormatState;
  private final boolean isBinaryFormat;

  SerializationFormat(final String description, final boolean isBinaryFormat) {
    outputFormatState = new OutputFormatState(name(), description);
    this.isBinaryFormat = isBinaryFormat;
  }

  @Override
  public String getDescription() {
    return outputFormatState.getDescription();
  }

  public String getFileExtension() {
    final List<String> formats = outputFormatState.getFormats();
    return formats.get(formats.size() - 1);
  }

  @Override
  public String getFormat() {
    return outputFormatState.getFormat();
  }

  @Override
  public List<String> getFormats() {
    return outputFormatState.getFormats();
  }

  /**
   * Instantiates and returns a new {@link CatalogSerializer} for this format.
   *
   * <p>Serializer classes (e.g. {@code JsonSerializedCatalog}) and their third-party library
   * imports are referenced only inside this method body, never in the enum constructor or static
   * initializer. This ensures that the enum can be loaded without triggering classloading of
   * optional serialization dependencies that may not be on the classpath in every deployment
   * context.
   *
   * @param catalog the catalog to serialize
   * @return a new serializer instance
   */
  public CatalogSerializer newSerializer(final Catalog catalog) {
    return switch (this) {
      case ser -> new JavaSerializedCatalog(catalog);
      case json -> new JsonSerializedCatalog(catalog);
      case yaml -> new YamlSerializedCatalog(catalog);
      case compact_json -> new CompactSerializedCatalog(catalog);
    };
  }

  public boolean isBinaryFormat() {
    return isBinaryFormat;
  }

  @Override
  public String toString() {
    return outputFormatState.toString();
  }
}
