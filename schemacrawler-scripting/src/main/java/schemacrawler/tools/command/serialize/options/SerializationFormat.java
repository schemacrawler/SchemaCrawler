/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
    } else {
      return outputFormat;
    }
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
