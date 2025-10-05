/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.serialize.options;

import static us.fatehi.utility.Utility.isBlank;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputFormatState;
import us.fatehi.utility.string.StringFormat;

public enum SerializationFormat implements OutputFormat {
  ser("Java serialization", "schemacrawler.tools.formatter.serialize.JavaSerializedCatalog", true),
  json(
      "JavaScript Object Notation (JSON) serialization format",
      "schemacrawler.tools.formatter.serialize.JsonSerializedCatalog",
      false),
  yaml(
      "YAML Ain't Markup Language (YAML) serialization format",
      "schemacrawler.tools.formatter.serialize.YamlSerializedCatalog",
      false),
  compact_json(
      "Compact JavaScript Object Notation (JSON) serialization format",
      "schemacrawler.tools.formatter.serialize.CompactSerializedCatalog",
      false),
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
  private final String serializerClassName;
  private final boolean isBinaryFormat;

  SerializationFormat(
      final String description, final String serializerClassName, final boolean isBinaryFormat) {
    outputFormatState = new OutputFormatState(name(), description);
    this.serializerClassName = serializerClassName;
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

  public String getSerializerClassName() {
    return serializerClassName;
  }

  public boolean isBinaryFormat() {
    return isBinaryFormat;
  }

  @Override
  public String toString() {
    return outputFormatState.toString();
  }
}
