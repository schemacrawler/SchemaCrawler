/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


/**
 * Methods to format entire rows of output.
 * 
 * @author Sualeh Fatehi
 */
public interface TextFormattingHelper
{

  enum DocumentHeaderType
  {

    title, subTitle, section;
  }

  /**
   * Creates an arrow symbol.
   * 
   * @return Arrow symbol
   */
  String createArrow();

  /**
   * Creates a definition row.
   * 
   * @param definition
   *        Definition
   * @return Row as a string
   */
  String createDefinitionRow(String definition);

  /**
   * Creates a description row with a blank spacer cells.
   * 
   * @param description
   *        Description
   * @return Row as a string
   */
  String createDescriptionRow(String description);

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
  String createDetailRow(String ordinal, String subName, String type);

  /**
   * Document end.
   * 
   * @return Document end
   */
  String createDocumentEnd();

  /**
   * Document start.
   * 
   * @return Document start
   */
  String createDocumentStart();

  /**
   * Create an empty row.
   * 
   * @return Row as a string
   */
  String createEmptyRow();

  /**
   * Creates a section header.
   * 
   * @param type
   *        Type of header
   * @param header
   *        Header text
   * @return Section header
   */
  String createHeader(DocumentHeaderType type, String header);

  /**
   * Create a name and description row.
   * 
   * @param name
   *        Name
   * @param description
   *        Description
   * @param underscore
   *        Whether to underscore the name row
   * @return Row as a string
   */
  String createNameRow(String name, String description, boolean underscore);

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
  String createNameValueRow(String name, String value, Alignment valueAlignment);

  /**
   * Database object end.
   * 
   * @return Database object end
   */
  String createObjectEnd();

  /**
   * Database object start.
   * 
   * @param name
   *        Object name
   * @return Database object start
   */
  String createObjectStart(String name);

  /**
   * Creates a row of data.
   * 
   * @param columnData
   *        Column data
   * @return Row of data
   */
  String createRow(String... columnData);

  /**
   * Creates a header row for data.
   * 
   * @param columnNames
   *        Column names
   * @return Header row for data
   */
  String createRowHeader(String... columnNames);

}
