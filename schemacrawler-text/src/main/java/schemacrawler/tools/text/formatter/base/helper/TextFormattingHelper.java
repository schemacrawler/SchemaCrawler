/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.text.formatter.base.helper;

import us.fatehi.utility.Color;
import us.fatehi.utility.html.Alignment;

/** Methods to format entire rows of output. */
public interface TextFormattingHelper {

  enum DocumentHeaderType {
    title {
      @Override
      String getHeaderTag() {
        return "h1";
      }

      @Override
      String getPrefix() {
        return "<p>&#160;</p>";
      }
    },
    subTitle {
      @Override
      String getHeaderTag() {
        return "h2";
      }

      @Override
      String getPrefix() {
        return "<p>&#160;</p>";
      }
    },
    section {
      @Override
      String getHeaderTag() {
        return "h3";
      }

      @Override
      String getPrefix() {
        return "";
      }
    };

    abstract String getHeaderTag();

    abstract String getPrefix();
  }

  TextFormattingHelper append(String text);

  /**
   * Creates a new anchor tag.
   *
   * @param text Anchor text
   * @param link Anchor link
   * @return Anchor tag
   */
  String createAnchor(String text, String link);

  /**
   * Creates an arrow symbol.
   *
   * @return Arrow symbol
   */
  String createLeftArrow();

  /**
   * Creates an arrow symbol.
   *
   * @return Arrow symbol
   */
  String createRightArrow();

  /**
   * Creates an arrow symbol.
   *
   * @return Arrow symbol
   */
  String createWeakLeftArrow();

  /**
   * Creates an arrow symbol.
   *
   * @return Arrow symbol
   */
  String createWeakRightArrow();

  void println();

  /**
   * Creates a description row with a blank spacer cells.
   *
   * @param description Description
   */
  void writeDescriptionRow(String description);

  /**
   * Creates a detail row, with four fields.
   *
   * @param text1 Ordinal value
   * @param text2 Name
   * @param text3 Type
   */
  void writeDetailRow(String text1, String text2, String text3);

  /**
   * Creates a detail row, with four fields. The name can be emphasized.
   *
   * @param text1 Text for field 1
   * @param text2 Text for field 2
   * @param text3 Text for field 3
   * @param escapeText Escape sequence
   * @param emphasize Whether to emphasize text
   * @param style Other CSS style
   */
  void writeDetailRow(
      String text1,
      String text2,
      String text3,
      boolean escapeText,
      boolean emphasize,
      String style);

  /** Document end. */
  void writeDocumentEnd();

  /** Document start. */
  void writeDocumentStart();

  /** Create an empty row. */
  void writeEmptyRow();

  /**
   * Creates a section header.
   *
   * @param type Type of header
   * @param header Header text
   */
  void writeHeader(DocumentHeaderType type, String header);

  /**
   * Create a name and description row.
   *
   * @param name Name
   * @param description Description
   */
  void writeNameRow(String name, String description);

  /**
   * Create a name and value row.
   *
   * @param name Name
   * @param value Value
   * @param valueAlignment Alignment of the value
   */
  void writeNameValueRow(String name, String value, Alignment valueAlignment);

  /** Database object end. */
  void writeObjectEnd();

  /**
   * Create a name and description row.
   *
   * @param id Unique identifier
   * @param name Name
   * @param description Description
   * @param backgroundColor Background color
   */
  void writeObjectNameRow(String id, String name, String description, Color backgroundColor);

  /** Database object start. */
  void writeObjectStart();

  /**
   * Creates a row of data.
   *
   * @param columnData Column data
   */
  void writeRow(Object... columnData);

  /**
   * Creates a header row for data.
   *
   * @param columnNames Column names
   */
  void writeRowHeader(String... columnNames);

  /**
   * Creates a definition row.
   *
   * @param definition Definition
   * @param style CSS style class
   */
  void writeWideRow(String definition, String style);
}
