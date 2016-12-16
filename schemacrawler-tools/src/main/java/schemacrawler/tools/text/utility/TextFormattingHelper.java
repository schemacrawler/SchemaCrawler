/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.text.utility;


import schemacrawler.tools.text.utility.html.Alignment;
import sf.util.Color;

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
   * Creates a detail row, with four fields.
   *
   * @param text1
   *        Ordinal value
   * @param text2
   *        Name
   * @param text3
   *        Type
   * @return Row as a string
   */
  void writeDetailRow(String text1, String text2, String text3);

  /**
   * Creates a detail row, with four fields. The name can be emphasized.
   *
   * @return Row as a string
   */
  void writeDetailRow(String text1,
                      String text2,
                      String text3,
                      boolean escapeText,
                      boolean emphasize,
                      String style);

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
