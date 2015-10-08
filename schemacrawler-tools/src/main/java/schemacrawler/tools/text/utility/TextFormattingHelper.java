/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools.text.utility;


import java.awt.Color;

import schemacrawler.tools.text.utility.html.Alignment;

/**
 * Methods to format entire rows of output.
 *
 * @author Sualeh Fatehi
 */
public interface TextFormattingHelper
{

  enum DocumentHeaderType
  {
   title
   {
     @Override
     String getHeaderTag()
     {
       return "h1";
     }

     @Override
     String getPrefix()
     {
       return "<p>&#160;</p>";
     }
   },
   subTitle
   {
     @Override
     String getHeaderTag()
     {
       return "h2";
     }

     @Override
     String getPrefix()
     {
       return "<p>&#160;</p>";
     }
   },
   section
   {
     @Override
     String getHeaderTag()
     {
       return "h3";
     }

     @Override
     String getPrefix()
     {
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
   * @param description
   *        Description
   * @return Row as a string
   */
  void writeDescriptionRow(String description);

  /**
   * Creates a detail row, with four fields. The name can be emphasized.
   *
   * @param ordinal
   *        Ordinal value
   * @param subName
   *        Name
   * @param escapeText
   *        TODO
   * @param type
   *        Type
   * @param emphasize
   *        Emphasize name.
   * @return Row as a string
   */
  void writeDetailRow(String ordinal,
                      String subName,
                      boolean escapeText,
                      String type,
                      boolean emphasize);

  /**
   * Creates a detail row, with four fields.
   *
   * @param ordinal
   *        Ordinal value
   * @param subName
   *        Name
   * @param type
   *        Type
   * @return Row as a string
   */
  void writeDetailRow(String ordinal, String subName, String type);

  /**
   * Document end.
   *
   * @return Document end
   */
  void writeDocumentEnd();

  /**
   * Document start.
   *
   * @return Document start
   */
  void writeDocumentStart();

  /**
   * Create an empty row.
   *
   * @return Row as a string
   */
  void writeEmptyRow();

  /**
   * Creates a section header.
   *
   * @param type
   *        Type of header
   * @param header
   *        Header text
   * @return Section header
   */
  void writeHeader(DocumentHeaderType type, String header);

  /**
   * Create a name and description row.
   *
   * @param name
   *        Name
   * @param description
   *        Description
   * @return Row as a string
   */
  void writeNameRow(String name, String description);

  /**
   * Create a name and value row.
   *
   * @param name
   *        Name
   * @param value
   *        Value
   * @param valueAlignment
   *        Alignment of the value
   * @return Row as a string
   */
  void writeNameValueRow(String name, String value, Alignment valueAlignment);

  /**
   * Database object end.
   *
   * @return Database object end
   */
  void writeObjectEnd();

  /**
   * Create a name and description row.
   *
   * @param id
   *        TODO
   * @param name
   *        Name
   * @param description
   *        Description
   * @return Row as a string
   */
  void writeObjectNameRow(String id,
                          String name,
                          String description,
                          Color backgroundColor);

  /**
   * Database object start.
   *
   * @return Database object start
   */
  void writeObjectStart();

  /**
   * Creates a row of data.
   *
   * @param columnData
   *        Column data
   * @return Row of data
   */
  void writeRow(Object... columnData);

  /**
   * Creates a header row for data.
   *
   * @param columnNames
   *        Column names
   * @return Header row for data
   */
  void writeRowHeader(String... columnNames);

  /**
   * Creates a definition row.
   *
   * @param definition
   *        Definition
   * @param style
   *        TODO
   * @return Row as a string
   */
  void writeWideRow(String definition, String style);

}
