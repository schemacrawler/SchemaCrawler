/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.util;



/**
 * Methods to format entire rows of output.
 * 
 * @author Sualeh Fatehi
 */
public interface TextFormattingHelper
{

  /**
   * Prints information.
   * 
   * @param object
   *        Object to print
   * @param id
   *        HTML id
   */
  public String createHeader(final String id, final Object object);

  /**
   * Called to handle the row output. Handler to be implemented by
   * subclass.
   * 
   * @param columnData
   *        Column data
   * @throws QueryExecutorException
   *         On an exception
   */
  public String createRow(final String[] columnData);

  /**
   * Called to handle the header output. Handler to be implemented by
   * subclass.
   * 
   * @param columnNames
   *        Column names
   * @throws QueryExecutorException
   *         On an exception
   */
  public String createRowHeader(final String[] columnNames);

  /**
   * Creates an arrow symbol.
   */
  String createArrow();

  /**
   * Creates a definition row with a pre-formatted definition.
   * 
   * @param definition
   *        Definition
   * @return Row as a string
   */
  String createDefinitionRow(final String definition);

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
  String createDetailRow(String ordinal, final String subName, final String type);

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
   * Create a name and description row.
   * 
   * @param name
   *        Name
   * @param description
   *        Description
   * @return Row as a string
   */
  String createNameRow(final String name,
                       final String description,
                       boolean underscore);

  /**
   * Create a name and value row.
   * 
   * @param name
   *        Name
   * @param value
   *        Value
   * @return Row as a string
   */
  String createNameValueRow(final String name, final String value);

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
  String createObjectStart(final String name);

  /**
   * Creates a pre-formatted text section.
   */
  String createPreformattedText(String id, String text);

}
